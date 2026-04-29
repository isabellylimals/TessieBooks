package com.isabelly.tessiebooks.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.dto.book.BookRequest;
import com.isabelly.tessiebooks.dto.book.BookResponse;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        BookResponse b = service.get(id);
        return b == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(b);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(@RequestParam String title) {
        return ResponseEntity.ok(service.search(title));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.ok("Livro removido.")
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookRequest req) {
        BookResponse b = service.update(id, req);
        return b == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(b);
    }


    @GetMapping("/recommendations")
    public ResponseEntity<List<BookResponse>> getRecommendations( Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.ok(List.of());
        }
        User user = (User) auth.getPrincipal();
        List<BookResponse> recommendations = service.getRecommendations(user.getId());
        return ResponseEntity.ok(recommendations);
    }
}