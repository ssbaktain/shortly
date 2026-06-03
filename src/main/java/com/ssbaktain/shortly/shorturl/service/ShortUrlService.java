package com.ssbaktain.shortly.shorturl.service;

import com.ssbaktain.shortly.common.util.Base62Encoder;
import com.ssbaktain.shortly.member.domain.Member;
import com.ssbaktain.shortly.member.service.MemberService;
import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlExpiredException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.shorturl.repository.ShortUrlRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final MemberService memberService;

    @Transactional
    public ShortUrl shorten(String originalUrl, Long memberId) {
        validateUrl(originalUrl);

        Member member = (memberId != null) ? memberService.getById(memberId) : null;

        ShortUrl shortUrl = ShortUrl.builder()
                .shortKey("temp")
                .originalUrl(originalUrl)
                .member(member)
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

    @Transactional(readOnly = true)
    public Page<ShortUrl> findMyUrls(Long memberId, Pageable pageable) {
        return shortUrlRepository.findByMemberId(memberId, pageable);
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
