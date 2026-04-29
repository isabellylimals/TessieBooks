package com.isabelly.tessiebooks.repository;

import com.isabelly.tessiebooks.entity.Chat;
import com.isabelly.tessiebooks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE :user MEMBER OF c.participants ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByUser(@Param("user") User user);
    
    @Query("SELECT c FROM Chat c WHERE SIZE(c.participants) = 2 AND :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    Optional<Chat> findPrivateChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
}
