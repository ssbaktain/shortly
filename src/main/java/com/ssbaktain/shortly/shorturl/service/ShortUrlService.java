package com.ssbaktain.shortly.shorturl.service;

import com.ssbaktain.shortly.common.util.Base62Encoder;
import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlExpiredException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.shorturl.repository.ShortUrlRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    @Transactional
    public ShortUrl shorten(String originalUrl) {
        validateUrl(originalUrl);

        ShortUrl shortUrl = ShortUrl.builder()
                .shortKey("temp")
                .originalUrl(originalUrl)
                .build();

        ShortUrl saved = shortUrlRepository.save(shortUrl);

        String shortKey = Base62Encoder.encode(saved.getId());
        saved.assignShortKey(shortKey);

        return saved;
    }

    @Transactional
    public ShortUrl getOriginalUrl(String shortKey) {
        ShortUrl shortUrl = shortUrlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortKey));

        if (shortUrl.isExpired()) {
            throw new ShortUrlExpiredException(shortKey);
        }

        shortUrl.increaseClickCount();
        return shortUrl;
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
    }
}
