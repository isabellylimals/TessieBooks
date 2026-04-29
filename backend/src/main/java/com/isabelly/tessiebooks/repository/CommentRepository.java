package com.isabelly.tessiebooks.repository;

import com.isabelly.tessiebooks.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByReviewIdOrderByCreatedAtAsc(Long reviewId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId")
    long countByReviewId(@Param("reviewId") Long reviewId);
}
