package com.isabelly.tessiebooks.dto.chat;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatDTO {
    private Long id;
    private String name;
    private Boolean isGroup;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ParticipantDTO> participants;
    private MessageDTO lastMessage;
    private Long unreadCount;
}
