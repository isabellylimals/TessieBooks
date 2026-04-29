package com.isabelly.tessiebooks.dto.book;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String description;
    private String genre;
    private String coverUrl;
    private Integer publicationYear;
    private Integer pages;
    private String keywords;
}
