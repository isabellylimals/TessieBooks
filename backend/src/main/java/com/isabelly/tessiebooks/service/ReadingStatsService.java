package com.isabelly.tessiebooks.service;

import com.isabelly.tessiebooks.dto.stats.*;
import com.isabelly.tessiebooks.entity.*;
import com.isabelly.tessiebooks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingStatsService {

    private final UserBookStatusRepository userBookStatusRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReadingStatsDTO getUserReadingStats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<UserBookStatus> readBooks = userBookStatusRepository
            .findByUserIdAndStatus(userId, ReadingStatus.LIDO);
        
        List<UserBookStatus> currentReading = userBookStatusRepository
            .findByUserIdAndStatus(userId, ReadingStatus.LENDO);

        ReadingStatsDTO stats = new ReadingStatsDTO();
        
        stats.setTotalBooksRead(readBooks.size());
        
        long totalPages = readBooks.stream()
            .mapToLong(ub -> ub.getPaginasTotais() != null ? ub.getPaginasTotais() : 0)
            .sum();
        stats.setTotalPagesRead(totalPages);
        
        stats.setAvgPagesPerBook(readBooks.isEmpty() ? 0 : (double) totalPages / readBooks.size());
        
        long totalDays = readBooks.stream()
            .filter(ub -> ub.getStartDate() != null && ub.getFinishDate() != null)
            .mapToLong(ub -> ChronoUnit.DAYS.between(ub.getStartDate(), ub.getFinishDate()))
            .sum();
        stats.setTotalReadingDays(totalDays);
        
        stats.setAvgTimePerBook(readBooks.isEmpty() ? 0 : (double) totalDays / readBooks.size());
        
        Map<String, Long> genreCount = new HashMap<>();
        Map<String, Long> genrePages = new HashMap<>();
        
        for (UserBookStatus ub : readBooks) {
            Book book = ub.getBook();
            if (book != null && book.getGenre() != null && !book.getGenre().isEmpty()) {
                String genre = book.getGenre();
                genreCount.merge(genre, 1L, Long::sum);
                if (ub.getPaginasTotais() != null) {
                    genrePages.merge(genre, ub.getPaginasTotais().longValue(), Long::sum);
                }
            }
        }
        
        List<GenreStatDTO> topGenres = genreCount.entrySet().stream()
            .map(e -> {
                GenreStatDTO dto = new GenreStatDTO();
                dto.setGenre(e.getKey());
                dto.setCount(e.getValue());
                dto.setTotalPages(genrePages.getOrDefault(e.getKey(), 0L));
                return dto;
            })
            .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
            .limit(5)
            .collect(Collectors.toList());
        stats.setTopGenres(topGenres);
        
        List<GenreEvolutionDTO> evolution = calculateGenreEvolution(readBooks);
        stats.setGenreEvolution(evolution);
        
        if (!currentReading.isEmpty()) {
            UserBookStatus current = currentReading.get(0);
            CurrentReadingDTO currentDTO = new CurrentReadingDTO();
            currentDTO.setBookId(current.getBook().getId());
            currentDTO.setBookTitle(current.getBook().getTitle());
            currentDTO.setCurrentPage(current.getPaginasLidas() != null ? current.getPaginasLidas() : 0);
            currentDTO.setTotalPages(current.getPaginasTotais() != null ? current.getPaginasTotais() : 0);
            int progress = currentDTO.getTotalPages() > 0 
                ? (int) ((double) currentDTO.getCurrentPage() / currentDTO.getTotalPages() * 100)
                : 0;
            currentDTO.setProgressPercent(progress);
            stats.setCurrentReading(currentDTO);
        }
        
        return stats;
    }
    
    private List<GenreEvolutionDTO> calculateGenreEvolution(List<UserBookStatus> readBooks) {
        Map<String, Map<String, Long>> evolutionByPeriod = new LinkedHashMap<>();
        
        for (UserBookStatus ub : readBooks) {
            if (ub.getFinishDate() == null || ub.getBook().getGenre() == null) continue;
            
            String period = getPeriodKey(ub.getFinishDate());
            String genre = ub.getBook().getGenre();
            
            evolutionByPeriod.putIfAbsent(period, new HashMap<>());
            evolutionByPeriod.get(period).merge(genre, 1L, Long::sum);
        }
        
        List<GenreEvolutionDTO> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> entry : evolutionByPeriod.entrySet()) {
            GenreEvolutionDTO dto = new GenreEvolutionDTO();
            dto.setPeriod(entry.getKey());
            dto.setGenres(entry.getValue());
            result.add(dto);
        }
        return result;
    }
    
    private String getPeriodKey(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3 + 1;
        return year + "-Q" + quarter;
    }
}
