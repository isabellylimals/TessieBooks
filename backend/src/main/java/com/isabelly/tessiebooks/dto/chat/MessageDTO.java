package com.isabelly.tessiebooks.dto.chat;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long chatId;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
