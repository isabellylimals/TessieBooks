package com.isabelly.tessiebooks.repository;

import java.util.List;
import java.util.Optional; // 1. Adicione este import

import org.springframework.data.jpa.repository.JpaRepository;

import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.UserBookStatus;

public interface UserBookStatusRepository extends JpaRepository<UserBookStatus, Long> {

    List<UserBookStatus> findByUserId(Long userId);
List<UserBookStatus> findByUserIdAndStatus(Long userId, ReadingStatus status);

    // 2. Coloque o Optional em volta do retorno
    Optional<UserBookStatus> findByUserIdAndBookId(Long userId, Long bookId);
}