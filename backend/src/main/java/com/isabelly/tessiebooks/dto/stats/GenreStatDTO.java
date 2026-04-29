package com.isabelly.tessiebooks.dto.stats;

import lombok.Data;

@Data
public class GenreStatDTO {
    private String genre;
    private long count;
    private long totalPages;
}
