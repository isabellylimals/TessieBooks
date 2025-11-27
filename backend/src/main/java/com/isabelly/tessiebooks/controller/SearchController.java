package com.isabelly.tessiebooks.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.SearchService;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public Object search(@RequestParam String query) {

        List<Book> books = searchService.searchBooks(query);
        List<User> users = searchService.searchUsers(query);

        return new Object() {
            public List<Book> booksFound = books;
            public List<User> usersFound = users;
        };
    }
}
