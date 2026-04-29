package com.isabelly.tessiebooks.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.Review;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.ReviewRepository;
import com.isabelly.tessiebooks.repository.UserRepository;
import com.isabelly.tessiebooks.service.ReviewService;
import com.isabelly.tessiebooks.service.UploadService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final UploadService uploadService;
private final UserRepository userRepository;

public ReviewController(ReviewService reviewService, BookRepository bookRepository, 
                        ReviewRepository reviewRepository, UploadService uploadService,
                        UserRepository userRepository) {
    this.reviewService = reviewService;
    this.bookRepository = bookRepository;
    this.reviewRepository = reviewRepository;
    this.uploadService = uploadService;
    this.userRepository = userRepository;
}
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            List<Map<String, Object>> response = new ArrayList<>();
            
            for (Review review : reviews) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("id", review.getId());
                reviewMap.put("title", review.getTitle());
                reviewMap.put("comment", review.getComment());
                reviewMap.put("rating", review.getRating());
                reviewMap.put("createdAt", review.getCreatedAt());
                reviewMap.put("imageUrl", review.getImageUrl());
                reviewMap.put("likes", review.getLikes() != null ? review.getLikes().size() : 0);
                
                if (review.getUser() != null) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", review.getUser().getId());
                    userMap.put("name", review.getUser().getName());
                    userMap.put("profileImage", review.getUser().getProfileImage());
                    reviewMap.put("user", userMap);
                }
                
                if (review.getBook() != null) {
                    Map<String, Object> bookMap = new HashMap<>();
                    bookMap.put("id", review.getBook().getId());
                    bookMap.put("title", review.getBook().getTitle());
                    bookMap.put("author", review.getBook().getAuthor());
                    reviewMap.put("book", bookMap);
                }
                
                response.add(reviewMap);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    // GET /reviews/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    // GET /reviews/book/{bookId}
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getBookReviews(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

    // POST /reviews
    @PostMapping
    @Transactional
public ResponseEntity<?> createReview(Authentication auth, 
                                      @RequestParam("bookId") Long bookId,
                                      @RequestParam("title") String title,
                                      @RequestParam("text") String text,
                                      @RequestParam(value = "rating", defaultValue = "5") Integer rating,
                                      @RequestParam(value = "image", required = false) MultipartFile image) {
    
    if (auth == null || auth.getPrincipal() == null) {
        return ResponseEntity.status(401).body("Você precisa estar logado");
    }
    
    try {
        User user = (User) auth.getPrincipal();
        
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = uploadService.saveFile(image);
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        
        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setTitle(title);
        review.setComment(text);
        review.setRating(rating);
        review.setImageUrl(imageUrl);
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setLikes(new ArrayList<>());
        
        Review saved = reviewRepository.save(review);
        
        return ResponseEntity.ok(saved);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao criar resenha: " + e.getMessage());
    }
}
    // DELETE /reviews/{id}
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

    // POST /reviews/{id}/like
 // POST /reviews/{id}/like
@PostMapping("/{id}/like")
public ResponseEntity<?> likeReview(@PathVariable Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    try {
        // Recarregar o usuário do banco para garantir dados atualizados
        User freshUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
        Review review = reviewService.likeReview(id, freshUser);
        Map<String, Object> response = new HashMap<>();
        response.put("likes", review.getLikes().size());
        response.put("liked", review.getLikes().contains(freshUser));
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
}

    // GET /reviews/{id}/has-liked
    @GetMapping("/{id}/has-liked")
    public ResponseEntity<?> hasUserLiked(@PathVariable Long id, Authentication auth) {
        User user = (User) auth.getPrincipal();
        try {
            boolean hasLiked = reviewService.hasUserLiked(id, user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("liked", hasLiked);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // GET /reviews/{id}/likes
    @GetMapping("/{id}/likes")
    public ResponseEntity<?> getReviewLikes(@PathVariable Long id) {
        try {
            List<User> likes = reviewService.getReviewLikes(id);
            List<Map<String, Object>> response = new ArrayList<>();
            for (User user : likes) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("profileImage", user.getProfileImage());
                response.add(userMap);
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
