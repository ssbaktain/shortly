package com.ssbaktain.shortly.shorturl.dto;

import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyUrlResponse {

    private String shortKey;
    private String shortUrl;
    private String originalUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public static MyUrlResponse from(ShortUrl shortUrl, String baseUrl) {
        return MyUrlResponse.builder()
                .shortKey(shortUrl.getShortKey())
                .shortUrl(baseUrl + "/" + shortUrl.getShortKey())
                .originalUrl(shortUrl.getOriginalUrl())
                .clickCount(shortUrl.getClickCount())
                .createdAt(shortUrl.getCreatedAt())
                .expiredAt(shortUrl.getExpiresAt())
                .build();
    }
}
