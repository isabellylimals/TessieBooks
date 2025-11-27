package com.isabelly.tessiebooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isabelly.tessiebooks.entity.UserBookStatus;

public interface UserBookStatusRepository extends JpaRepository<UserBookStatus, Long> {

    List<UserBookStatus> findByUserId(Long userId);

    UserBookStatus findByUserIdAndBookId(Long userId, Long bookId);
}
