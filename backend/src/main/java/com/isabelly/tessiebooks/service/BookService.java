package com.isabelly.tessiebooks.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.dto.book.BookRequest;
import com.isabelly.tessiebooks.dto.book.BookResponse;
import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    private BookResponse toDTO(Book b) {
        BookResponse dto = new BookResponse();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());
        dto.setDescription(b.getDescription());
        dto.setGenre(b.getGenre());
        dto.setCoverUrl(b.getCoverImageUrl());
        return dto;
    }

    public BookResponse create(BookRequest req) {
        Book b = new Book();
        b.setTitle(req.getTitle());
        b.setAuthor(req.getAuthor());
        b.setDescription(req.getDescription());
        b.setGenre(req.getGenre());
        b.setCoverImageUrl(req.getCoverUrl());
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

    public BookResponse update(Long id, BookRequest req) {
        Book b = repo.findById(id).orElse(null);
        if (b == null) return null;

        b.setTitle(req.getTitle());
        b.setAuthor(req.getAuthor());
        b.setDescription(req.getDescription());
        b.setGenre(req.getGenre());
        b.setCoverImageUrl(req.getCoverUrl());

        repo.save(b);

        return toDTO(b);
    }
}
