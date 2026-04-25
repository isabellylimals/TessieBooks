package com.isabelly.tessiebooks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
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
public ResponseEntity<?> search(@RequestParam String query) {
    
    if (query == null || query.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Query de busca não pode estar vazia");
    }
    
    List<Book> books = searchService.searchBooks(query);
    List<User> users = searchService.searchUsers(query);
    
    Map<String, Object> response = Map.of(
        "booksFound", books,
        "usersFound", users
    );
    
    return ResponseEntity.ok(response);
}
}
