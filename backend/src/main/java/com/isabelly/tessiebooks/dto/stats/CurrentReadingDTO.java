package com.isabelly.tessiebooks.dto.stats;

import lombok.Data;

@Data
public class CurrentReadingDTO {
    private Long bookId;
    private String bookTitle;
    private int currentPage;
    private int totalPages;
    private int progressPercent;
}
