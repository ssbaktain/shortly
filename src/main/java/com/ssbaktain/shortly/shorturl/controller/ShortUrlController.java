package com.ssbaktain.shortly.shorturl.controller;

import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.dto.AccessRequest;
import com.ssbaktain.shortly.shorturl.dto.MyUrlResponse;
import com.ssbaktain.shortly.shorturl.dto.ShortenRequest;
import com.ssbaktain.shortly.shorturl.dto.ShortenResponse;
import com.ssbaktain.shortly.shorturl.service.ShortUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/api/urls")
    public ResponseEntity<ShortenResponse> shorten(
            @Valid @RequestBody ShortenRequest request,
            @AuthenticationPrincipal Long memberId) {
        ShortUrl shortUrl = shortUrlService.shorten(
                request.getOriginalUrl(),
                memberId,
                request.getExpiresAt(),
                request.getPassword());
        ShortenResponse response = ShortenResponse.from(shortUrl, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/urls")
    public ResponseEntity<Page<MyUrlResponse>> myUrls(
            @AuthenticationPrincipal Long memberId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<MyUrlResponse> response = shortUrlService.findMyUrls(memberId, pageable)
                        .map(s -> MyUrlResponse.from(s, baseUrl));
                return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {
        ShortUrl shortUrl = shortUrlService.getOriginalUrl(shortKey, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(shortUrl.getOriginalUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/{shortKey}")
    public ResponseEntity<Void> redirectWithPassword(
            @PathVariable String shortKey,
            @Valid @RequestBody AccessRequest request) {
        ShortUrl shortUrl = shortUrlService.getOriginalUrl(shortKey, request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(shortUrl.getOriginalUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
