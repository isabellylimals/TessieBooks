package com.isabelly.tessiebooks.controller;

import com.isabelly.tessiebooks.dto.comment.CommentRequestDTO;
import com.isabelly.tessiebooks.dto.comment.CommentResponseDTO;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReview(reviewId));
    }
    
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long reviewId,
            @RequestBody CommentRequestDTO request,
            Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(commentService.createComment(reviewId, user.getId(), request));
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok("Comentário deletado com sucesso");
    }
}
