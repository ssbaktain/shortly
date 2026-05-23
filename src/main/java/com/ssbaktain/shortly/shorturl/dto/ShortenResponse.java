package com.ssbaktain.shortly.shorturl.dto;

import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortenResponse {

    private String shortKey;
    private String shortUrl;
    private String originalUrl;

    public static ShortenResponse from(ShortUrl shortUrl, String baseUrl) {
        return ShortenResponse.builder()
                .shortKey(shortUrl.getShortKey())
                .shortUrl(baseUrl + "/" + shortUrl.getShortKey())
                .originalUrl(shortUrl.getOriginalUrl())
                .build();
    }
}
