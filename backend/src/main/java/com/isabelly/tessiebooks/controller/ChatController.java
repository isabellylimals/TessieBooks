package com.isabelly.tessiebooks.controller;

import com.isabelly.tessiebooks.dto.chat.*;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @GetMapping
    public ResponseEntity<List<ChatDTO>> getUserChats(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(chatService.getUserChats(user.getId()));
    }
    
    @PostMapping("/private/{userId}")
    public ResponseEntity<?> createPrivateChat(@PathVariable Long userId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        ChatDTO chat = chatService.createPrivateChat(user.getId(), userId);
        return ResponseEntity.ok(chat);
    }
    
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long chatId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(chatService.getChatMessages(chatId, user.getId()));
    }
    
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest request,
            Authentication auth) {
        User user = (User) auth.getPrincipal();
        MessageDTO message = chatService.sendMessage(chatId, user.getId(), request.getContent());
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/{chatId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long chatId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        chatService.markMessagesAsRead(chatId, user.getId());
        return ResponseEntity.ok().build();
    }
}
