package com.isabelly.tessiebooks.service;

import com.isabelly.tessiebooks.dto.chat.*;
import com.isabelly.tessiebooks.entity.*;
import com.isabelly.tessiebooks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ChatDTO createPrivateChat(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1).orElseThrow();
        User user2 = userRepository.findById(userId2).orElseThrow();
        
        var existingChat = chatRepository.findPrivateChatBetweenUsers(user1, user2);
        if (existingChat.isPresent()) {
            return toChatDTO(existingChat.get(), userId1);
        }
        
        Chat chat = new Chat();
        chat.setIsGroup(false);
        chat.setName(null);
        chat.getParticipants().add(user1);
        chat.getParticipants().add(user2);
        
        Chat savedChat = chatRepository.save(chat);
        return toChatDTO(savedChat, userId1);
    }
    
    @Transactional
    public MessageDTO sendMessage(Long chatId, Long senderId, String content) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();
        
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setChat(chat);
        message.setIsRead(false);
        
        chat.setUpdatedAt(java.time.LocalDateTime.now());
        chatRepository.save(chat);
        
        Message savedMessage = messageRepository.save(message);
        return toMessageDTO(savedMessage);
    }
    
    @Transactional(readOnly = true)
    public List<ChatDTO> getUserChats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return chatRepository.findChatsByUser(user).stream()
            .map(chat -> toChatDTO(chat, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MessageDTO> getChatMessages(Long chatId, Long userId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId).stream()
            .map(this::toMessageDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        messageRepository.markMessagesAsRead(chatId, userId);
    }
    
    private ChatDTO toChatDTO(Chat chat, Long currentUserId) {
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setIsGroup(chat.getIsGroup());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());
        
        List<ParticipantDTO> participants = chat.getParticipants().stream()
            .map(this::toParticipantDTO)
            .collect(Collectors.toList());
        dto.setParticipants(participants);
        
        if (!chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setLastMessage(toMessageDTO(lastMessage));
        }
        
        long unreadCount = messageRepository.countUnreadMessages(chat.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);
        
        if (!chat.getIsGroup() && participants.size() == 2) {
            ParticipantDTO other = participants.stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);
            if (other != null) {
                dto.setName(other.getName());
            }
        }
        
        return dto;
    }
    
    private MessageDTO toMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setSenderAvatar(message.getSender().getProfileImage());
        dto.setChatId(message.getChat().getId());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setIsRead(message.getIsRead());
        return dto;
    }
    
    private ParticipantDTO toParticipantDTO(User user) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setProfileImage(user.getProfileImage());
        return dto;
    }
}
