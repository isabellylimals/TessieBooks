package com.isabelly.tessiebooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.Review;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findByOrderByCreatedAtDesc();
    }

    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Review> getBookReviews(Long bookId) {
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    public Review createReview(User user, Long bookId, String title, String comment, int rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setTitle(title);
        review.setComment(comment);
        review.setRating(rating);

        return reviewRepository.save(review);
    }

    public Review createReviewWithImage(User user, Long bookId, String title, String comment, int rating, String imageUrl) {
        Review review = createReview(user, bookId, title, comment, rating);
        review.setImageUrl(imageUrl);
        return reviewRepository.save(review);
    }

    public Review deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review não encontrada"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Você não pode deletar reviews de outros usuários");
        }

        reviewRepository.delete(review);
        return review;
    }


// Contar quantos likes
public int getLikesCount(Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Resenha não encontrada"));
    return review.getLikes().size();
}

// Verificar se usuário já curtiu
public boolean hasUserLiked(Long reviewId, Long userId) {
    Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Resenha não encontrada"));
    return review.getLikes().stream().anyMatch(u -> u.getId().equals(userId));
}
public List<User> getReviewLikes(Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Resenha não encontrada"));
    return review.getLikes();
}

public Review likeReview(Long reviewId, User user) {
    Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Resenha não encontrada"));
    
    if (review.getLikes().contains(user)) {
        review.getLikes().remove(user);
    } else {
        review.getLikes().add(user);
    }
    
    return reviewRepository.save(review);
}
}
