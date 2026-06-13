package com.ssbaktain.shortly.shorturl.service;

import com.ssbaktain.shortly.member.service.MemberService;
import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordRequiredException;
import com.ssbaktain.shortly.shorturl.repository.ShortUrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    @DisplayName("shorten() - null URL은 IllegalArgumentException")
    void shorten_throws_when_url_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> shortUrlService.shorten(null, null, null, null));
    }

    @Test
    @DisplayName("getOriginalUrl() - shortKey 없으면 ShortUrlNotFoundException")
    void getOriginalUrl_throws_when_short_key_not_found() {
        when(shortUrlRepository.findByShortKey("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(ShortUrlNotFoundException.class,
                () -> shortUrlService.getOriginalUrl("unknown", null));

        verify(shortUrlRepository, never()).incrementClickCount(anyLong());
    }

    @Test
    @DisplayName("getOriginalUrl() - 비번 보호 + apssword null -> PasswordRequired")
    void getOriginalUrl_throws_password_required_when_protected() {
        ShortUrl protectedUrl = ShortUrl.builder()
                .shortKey("abc1234")
                .originalUrl("https://www.example.com")
                .passwordHash("$2a$10$dummyhash")
                .build();
        when(shortUrlRepository.findByShortKey("abc1234"))
                .thenReturn(Optional.of(protectedUrl));

        assertThrows(ShortUrlPasswordRequiredException.class,
                () -> shortUrlService.getOriginalUrl("abc1234", null));

        verify(shortUrlRepository, never()).incrementClickCount(anyLong());
    }

    @Test
    @DisplayName("getOriginalUrl() - 정상 흐름은 click_count를 증가시킨다")
    void getOriginalUrl_increments_click_count_con_success() {
        ShortUrl plainUrl = ShortUrl.builder()
                .shortKey("abc1234")
                .originalUrl("https://www.example.com")
                .build();
        when(shortUrlRepository.findByShortKey("abc1234"))
                .thenReturn(Optional.of(plainUrl));

        ShortUrl result = shortUrlService.getOriginalUrl("abc1234", null);

        assertEquals("https://www.example.com", result.getOriginalUrl());
        verify(shortUrlRepository).incrementClickCount(any());
    }
}
