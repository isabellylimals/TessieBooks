package com.isabelly.tessiebooks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isabelly.tessiebooks.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    //User findByEmail(String email);

    List<User> findByNameContainingIgnoreCase(String q);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

}
