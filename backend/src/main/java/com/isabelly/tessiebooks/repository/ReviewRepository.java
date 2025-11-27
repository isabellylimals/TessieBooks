package com.isabelly.tessiebooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isabelly.tessiebooks.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByOrderByCreatedAtDesc();
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Review> findByBookIdOrderByCreatedAtDesc(Long bookId);
}
