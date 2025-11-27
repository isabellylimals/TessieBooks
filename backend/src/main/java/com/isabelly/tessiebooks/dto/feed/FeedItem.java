package com.isabelly.tessiebooks.dto.feed;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FeedItem {
    private String user;
    private String book;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
