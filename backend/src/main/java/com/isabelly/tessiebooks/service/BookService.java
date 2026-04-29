package com.isabelly.tessiebooks.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.isabelly.tessiebooks.entity.User;
import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.dto.book.BookRequest;
import com.isabelly.tessiebooks.dto.book.BookResponse;
import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserBookStatusRepository;
import com.isabelly.tessiebooks.repository.UserRepository;

@Service
public class BookService {

    private final BookRepository repo;
    private final UserRepository userRepository;
    private final UserBookStatusRepository userBookStatusRepository;
    public BookService(BookRepository repo, UserRepository userRepository, UserBookStatusRepository userBookStatusRepository) {
    this.repo = repo;
    this.userRepository = userRepository;
    this.userBookStatusRepository = userBookStatusRepository;
}

    private BookResponse toDTO(Book b) {
        BookResponse dto = new BookResponse();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());
        dto.setDescription(b.getDescription());
        dto.setGenre(b.getGenre());
        dto.setCoverUrl(b.getCoverImageUrl());
        dto.setPublicationYear(b.getPublicationYear());
        dto.setPages(b.getPages());
        dto.setKeywords(b.getKeywords());
        return dto;
    }

    public BookResponse create(BookRequest req) {
        Book b = new Book();
        b.setTitle(req.getTitle());
        b.setAuthor(req.getAuthor());
        b.setDescription(req.getDescription());
        b.setGenre(req.getGenre());
        b.setCoverImageUrl(req.getCoverUrl());
        b.setPublicationYear(req.getPublicationYear());
        b.setPages(req.getPages());
        b.setKeywords(req.getKeywords());
        repo.save(b);
        return toDTO(b);
    }

    public List<BookResponse> list() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BookResponse get(Long id) {
        Book b = repo.findById(id).orElse(null);
        return b == null ? null : toDTO(b);
    }

    public List<BookResponse> search(String title) {
        return repo.findByTitleContainingIgnoreCase(title)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
public List<BookResponse> getRecommendations(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        
        // Buscar livros que o usuário já leu ou está lendo
        List<Long> userBookIds = userBookStatusRepository.findByUserId(userId).stream()
            .map(ub -> ub.getBook().getId())
            .collect(Collectors.toList());
        
        // Coletar tags dos livros do usuário
        Map<String, Integer> tagScore = new HashMap<>();
        for (Long bookId : userBookIds) {
            Book book = repo.findById(bookId).orElse(null);
            if (book != null && book.getKeywords() != null && !book.getKeywords().isEmpty()) {
                String[] tags = book.getKeywords().toLowerCase().split(",");
                for (String tag : tags) {
                    String trimmedTag = tag.trim();
                    tagScore.put(trimmedTag, tagScore.getOrDefault(trimmedTag, 0) + 1);
                }
            }
        }
        
        // Buscar livros não lidos com tags similares
        List<Book> allBooks = repo.findAll();
        List<BookRecommendation> recommendations = new ArrayList<>();
        
        for (Book book : allBooks) {
            if (userBookIds.contains(book.getId())) continue;
            
            int score = 0;
            if (book.getKeywords() != null && !book.getKeywords().isEmpty()) {
                String[] tags = book.getKeywords().toLowerCase().split(",");
                for (String tag : tags) {
                    String trimmedTag = tag.trim();
                    score += tagScore.getOrDefault(trimmedTag, 0);
                }
            }
            
            // Também considerar gênero
            if (book.getGenre() != null && tagScore.containsKey(book.getGenre().toLowerCase())) {
                score += tagScore.getOrDefault(book.getGenre().toLowerCase(), 0) * 2;
            }
            
            if (score > 0) {
                recommendations.add(new BookRecommendation(book, score));
            }
        }
        
        // Ordenar por pontuação e retornar top 10
        recommendations.sort((a, b) -> b.score - a.score);
        return recommendations.stream()
            .limit(10)
            .map(r -> toDTO(r.book))
            .collect(Collectors.toList());
    }
    
    private static class BookRecommendation {
        Book book;
        int score;
        BookRecommendation(Book book, int score) {
            this.book = book;
            this.score = score;
        }
    }
    public BookResponse update(Long id, BookRequest req) {
        Book b = repo.findById(id).orElse(null);
        if (b == null) return null;

        b.setTitle(req.getTitle());
        b.setAuthor(req.getAuthor());
        b.setDescription(req.getDescription());
        b.setGenre(req.getGenre());
        b.setCoverImageUrl(req.getCoverUrl());
        b.setPublicationYear(req.getPublicationYear());
        b.setPages(req.getPages());
        b.setKeywords(req.getKeywords());

        repo.save(b);

        return toDTO(b);
    }
}
