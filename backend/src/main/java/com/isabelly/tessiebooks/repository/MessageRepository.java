package com.isabelly.tessiebooks.repository;

import com.isabelly.tessiebooks.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.sender.id != :userId")
    void markMessagesAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.isRead = false AND m.sender.id != :userId")
    long countUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
