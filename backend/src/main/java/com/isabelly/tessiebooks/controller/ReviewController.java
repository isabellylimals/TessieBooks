package com.isabelly.tessiebooks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.Review;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // GET /reviews - retorna todas as reviews
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // GET /reviews/user/{userId} - retorna reviews de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    // GET /reviews/book/{bookId} - retorna reviews de um livro
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookReviews(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

    // POST /reviews - criar uma nova review
    @PostMapping
    public ResponseEntity<?> createReview(Authentication auth, @RequestBody Map<String, Object> req) {
        User user = (User) auth.getPrincipal();

        Long bookId = ((Number) req.get("bookId")).longValue();
        String title = (String) req.get("title");
        String text = (String) req.get("text");
        Integer rating = (Integer) req.getOrDefault("rating", 5);
        String imageUrl = (String) req.get("imageUrl"); // nova foto

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comentário não pode estar vazio");
        }

        Review review;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            review = reviewService.createReviewWithImage(user, bookId, title, text, rating, imageUrl);
        } else {
            review = reviewService.createReview(user, bookId, title, text, rating);
        }
        return ResponseEntity.ok(review);
    }

    // DELETE /reviews/{id} - deletar uma review
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();

        try {
            reviewService.deleteReview(id, user);
            return ResponseEntity.ok("Review deletada com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // POST /reviews/{id}/like - dar like em uma review
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeReview(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();

        try {
            Review review = reviewService.likeReview(id, user);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
