package com.isabelly.tessiebooks.controller;

import com.isabelly.tessiebooks.dto.stats.ReadingStatsDTO;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.ReadingStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class ReadingStatsController {

    private final ReadingStatsService statsService;

    @GetMapping("/me")
    public ResponseEntity<ReadingStatsDTO> getMyStats(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(statsService.getUserReadingStats(user.getId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ReadingStatsDTO> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(statsService.getUserReadingStats(userId));
    }
}
