package com.isabelly.tessiebooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isabelly.tessiebooks.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);
}
