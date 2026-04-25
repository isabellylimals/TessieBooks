package com.isabelly.tessiebooks.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserBookStatusRepository;

@Service
public class UserBookStatusService {

    private final UserBookStatusRepository repo;
    private final BookRepository bookRepo;

    public UserBookStatusService(UserBookStatusRepository repo, BookRepository bookRepo) {
        this.repo = repo;
        this.bookRepo = bookRepo;
    }

    public UserBookStatus updateStatus(User user, Long bookId, ReadingStatus status) {

    Book book = bookRepo.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

    UserBookStatus s = repo.findByUserIdAndBookId(user.getId(), bookId)
            .orElse(null);
    
    if (s == null) {
        s = new UserBookStatus();
        s.setUser(user);
        s.setBook(book);
    }

    s.setStatus(status);

    if (status == ReadingStatus.LENDO) {
        s.setStartDate(LocalDate.now());
    }

    if (status == ReadingStatus.LIDO) {
        s.setFinishDate(LocalDate.now());
    }

    return repo.save(s);
}

    public List<UserBookStatus> getLibrary(Long userId) {
        return repo.findByUserId(userId);
    }
}
