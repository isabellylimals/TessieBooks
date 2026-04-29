package com.isabelly.tessiebooks.service;

import com.isabelly.tessiebooks.dto.comment.CommentRequestDTO;
import com.isabelly.tessiebooks.dto.comment.CommentResponseDTO;
import com.isabelly.tessiebooks.entity.Comment;
import com.isabelly.tessiebooks.entity.Review;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.CommentRepository;
import com.isabelly.tessiebooks.repository.ReviewRepository;
import com.isabelly.tessiebooks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public CommentResponseDTO createComment(Long reviewId, Long userId, CommentRequestDTO request) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Resenha não encontrada"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setReview(review);
        
        Comment saved = commentRepository.save(comment);
        return toResponseDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByReview(Long reviewId) {
        return commentRepository.findByReviewIdOrderByCreatedAtAsc(reviewId).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Você só pode deletar seus próprios comentários");
        }
        
        commentRepository.delete(comment);
    }
    
    private CommentResponseDTO toResponseDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setUserAvatar(comment.getUser().getProfileImage());
        dto.setReviewId(comment.getReview().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
