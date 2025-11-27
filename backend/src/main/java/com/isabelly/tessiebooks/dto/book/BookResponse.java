package com.isabelly.tessiebooks.dto.book;

import lombok.Data;

@Data
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private String coverUrl;
}
