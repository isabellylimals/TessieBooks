package com.isabelly.tessiebooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserRepository;


@Service
public class SearchService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public SearchService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> searchBooks(String q) {
        return bookRepository.findByTitleContainingIgnoreCase(q);
    }

    public List<User> searchUsers(String q) {
        return userRepository.findByNameContainingIgnoreCase(q);
    }
}
