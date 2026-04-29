package com.isabelly.tessiebooks.dto.comment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long reviewId;
    private LocalDateTime createdAt;
}
