package com.isabelly.tessiebooks.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import 
org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;    
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import com.isabelly.tessiebooks.service.AuthService;
import com.isabelly.tessiebooks.service.LibraryService;
import com.isabelly.tessiebooks.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final AuthService authService;
    private final UserService userService;

    public LibraryController(LibraryService libraryService, AuthService authService, UserService userService) {
        this.libraryService = libraryService;
        this.authService = authService;
        this.userService = userService;
    }

    // Define/atualiza status de um livro na minha biblioteca
    @PostMapping("/books/{bookId}/status")
    public ResponseEntity<?> setStatus(
            @PathVariable Long bookId,
            @RequestParam String status,
            HttpServletRequest req
    ) {
        User me = authService.getAuthenticatedUser(req);

        ReadingStatus rs;
        try {
            rs = ReadingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Status inválido. Use: TO_READ, READING ou READ");
        }

        UserBookStatus ub = libraryService.setStatus(me, bookId, rs);
        return ResponseEntity.ok(ub);
    }

    // Minha biblioteca completa
    @GetMapping("/me")
    public ResponseEntity<List<UserBookStatus>> myLibrary(
            @RequestParam(required = false) String status,
            HttpServletRequest req
    ) {
        User me = authService.getAuthenticatedUser(req);

        if (status == null) {
            return ResponseEntity.ok(libraryService.getLibrary(me));
        }

        ReadingStatus rs = ReadingStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(libraryService.getLibraryByStatus(me, rs));
    }


}
