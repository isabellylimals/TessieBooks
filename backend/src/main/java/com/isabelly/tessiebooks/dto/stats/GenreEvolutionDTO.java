package com.isabelly.tessiebooks.dto.stats;

import lombok.Data;
import java.util.Map;

@Data
public class GenreEvolutionDTO {
    private String period;
    private Map<String, Long> genres;
}
