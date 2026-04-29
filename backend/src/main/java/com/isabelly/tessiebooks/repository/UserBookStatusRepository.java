package com.isabelly.tessiebooks.repository;

import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookStatusRepository extends JpaRepository<UserBookStatus, Long> {
    
    List<UserBookStatus> findByUserId(Long userId);
    
    List<UserBookStatus> findByUserIdAndStatus(Long userId, ReadingStatus status);
    
    Optional<UserBookStatus> findByUserIdAndBookId(Long userId, Long bookId);
    
    List<UserBookStatus> findByBookId(Long bookId);
    
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
