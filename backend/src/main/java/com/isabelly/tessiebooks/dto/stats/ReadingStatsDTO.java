package com.isabelly.tessiebooks.dto.stats;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReadingStatsDTO {
    private long totalBooksRead;
    private long totalPagesRead;
    private double avgPagesPerBook;
    private long totalReadingDays;
    private double avgTimePerBook;
    private String preferredReadingHour;
    private Map<String, Integer> readingByHour;
    private List<GenreStatDTO> topGenres;
    private List<GenreEvolutionDTO> genreEvolution;
    private CurrentReadingDTO currentReading;
}
