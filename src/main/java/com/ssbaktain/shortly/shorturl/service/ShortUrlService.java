package com.ssbaktain.shortly.shorturl.service;

import com.ssbaktain.shortly.member.domain.Member;
import com.ssbaktain.shortly.member.service.MemberService;
import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlExpiredException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordMismatchException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordRequiredException;
import com.ssbaktain.shortly.shorturl.repository.ShortUrlRepository;
import com.ssbaktain.shortly.shorturl.util.ShortKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_RETRY = 3;

    @Transactional
    public ShortUrl shorten(String originalUrl,
                            Long memberId,
                            LocalDateTime expiresAt,
                            String password) {
        validateUrl(originalUrl);

        Member member = (memberId != null) ? memberService.getById(memberId) : null;
        String passwordHash = (password != null)? passwordEncoder.encode(password) : null;

        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            String shortKey = ShortKeyGenerator.generate();
            ShortUrl shortUrl = ShortUrl.builder()
                    .shortKey(shortKey)
                    .originalUrl(originalUrl)
                    .member(member)
                    .passwordHash(passwordHash)
                    .expiresAt(expiresAt)
                    .build();

            try {
                return shortUrlRepository.save(shortUrl);
            } catch (DataIntegrityViolationException e) {
                log.warn("shortKey collision on attempt {} - retrying", attempt + 1);
            }
        }
        throw new IllegalStateException(
                "Failed to generate unique shorKey after " + MAX_RETRY + " attempts");
    }

    @Transactional
    public ShortUrl getOriginalUrl(String shortKey, String password) {
        ShortUrl shortUrl = shortUrlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortKey));

        if (shortUrl.isExpired()) {
            throw new ShortUrlExpiredException(shortKey);
        }

        if (shortUrl.isPasswordProtected()) {
            if (password == null) {
                throw new ShortUrlPasswordRequiredException(shortKey);
            }
            if (!passwordEncoder.matches(password, shortUrl.getPasswordHash())) {
                throw new ShortUrlPasswordMismatchException(shortKey);
            }
        }

        shortUrlRepository.incrementClickCount(shortUrl.getId());
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
