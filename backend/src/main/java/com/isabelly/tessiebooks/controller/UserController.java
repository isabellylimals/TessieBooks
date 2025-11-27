package com.isabelly.tessiebooks.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // LISTAR TODOS OS USUÁRIOS (público)
    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // SEGUIR OUTRO USUÁRIO
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long id, Authentication auth) {
        User me = (User) auth.getPrincipal();
        try {
            userService.follow(me.getId(), id);
            return ResponseEntity.ok("Seguido com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DEIXAR DE SEGUIR
    @PostMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long id, Authentication auth) {
        User me = (User) auth.getPrincipal();
        try {
            userService.unfollow(me.getId(), id);
            return ResponseEntity.ok("Deixou de seguir");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ---- GET /users/me ----
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        User me = (User) auth.getPrincipal();
        return ResponseEntity.ok(me);
    }

    // ---- GET /users/{id} ----
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // ---- PUT /users/me ----
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, String> updates) {
        User me = (User) auth.getPrincipal();
        User updated = userService.updateProfile(me.getId(), updates);
        return ResponseEntity.ok(updated);
    }

}
