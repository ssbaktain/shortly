package com.ssbaktain.shortly.shorturl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssbaktain.shortly.shorturl.domain.ShortUrl;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlExpiredException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlNotFoundException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordMismatchException;
import com.ssbaktain.shortly.shorturl.exception.ShortUrlPasswordRequiredException;
import com.ssbaktain.shortly.shorturl.service.ShortUrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortUrlController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.base-url=http://localhost:9876")
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private ShortUrlService shortUrlService;

    @Test
    @DisplayName("POST /api/urls - 정상 요청 시 201 + shortKey/shortUrl/originalUrl 반환")
    void shorten_returns_201_with_response_body() throws Exception {
        ShortUrl mockShortUrl = ShortUrl.builder()
                .shortKey("abc1234")
                .originalUrl("https://www.example.com")
                .build();
        when(shortUrlService.shorten(any(), any(), any(), any()))
                .thenReturn(mockShortUrl);

        String json = objectMapper.writeValueAsString(Map.of("originalUrl", "https://www.example.com"));

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortKey").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:9876/abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    @Test
    @DisplayName("POST /api/urls - originalUrl 빈 문자열이면 400 + Bad Request + service 호출 안됨")
    void shorten_returns_400_when_originalUrl_blank() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("originalUrl", ""));

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("originalUrl: Original URL must not be blank"))
                .andExpect(jsonPath("$.path").value("/api/urls"));

        verify(shortUrlService, never()).shorten(any(), any(), any(), any());
    }

    @Test
    @DisplayName("GET /{shortKey} - 정상 키면 302 + Location 헤더에 원본 URL")
    void redirect_returns_302_with_location_header() throws Exception {
        ShortUrl mockShortUrl = ShortUrl.builder()
                .shortKey("abc1234")
                .originalUrl("https://www.example.com")
                .build();
        when(shortUrlService.getOriginalUrl("abc1234", null))
                .thenReturn(mockShortUrl);

        mockMvc.perform(get("/abc1234"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.example.com"));
    }

    @Test
    @DisplayName("GET /{shortKey} - 존재하지 않는 키면 404 + Not Found")
    void redirect_returns_404_when_shortKey_not_found() throws Exception {
        when(shortUrlService.getOriginalUrl("noexist", null))
                .thenThrow(new ShortUrlNotFoundException("noexist"));

        mockMvc.perform(get("/noexist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Short URL not found: noexist"));
    }

    @Test
    @DisplayName("GET /{shortKey} - 만료된 키면 410 + Gone")
    void redirect_returns_410_when_expired() throws Exception {
        when(shortUrlService.getOriginalUrl("expired", null))
                .thenThrow(new ShortUrlExpiredException("expired"));

        mockMvc.perform(get("/expired"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.error").value("Gone"));
    }

    @Test
    @DisplayName("GET /{shortKey} - 비번 보호 URL에 비번 없이 접근하면 401 + Unauthorized")
    void redirect_returns_401_when_password_required() throws Exception {
        when(shortUrlService.getOriginalUrl("protected", null))
                .thenThrow(new ShortUrlPasswordRequiredException("protected"));

        mockMvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /{shortKey} - 비번 맞으면 302 + Location 헤더")
    void redirect_with_password_returns_302_when_password_matches() throws Exception {
        ShortUrl mockShortUrl = ShortUrl.builder()
                .shortKey("protected")
                .originalUrl("https://www.secret.com")
                .build();
        when(shortUrlService.getOriginalUrl("protected", "password123"))
                .thenReturn(mockShortUrl);

        String json = objectMapper.writeValueAsString(Map.of("password", "password123"));

        mockMvc.perform(post("/protected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.secret.com"));
    }

    @Test
    @DisplayName("POST /{shortKey} - 비번 틀리면 401 + Unauthorized")
    void redirect_with_password_returns_401_when_password_mismatches() throws Exception {
        when(shortUrlService.getOriginalUrl("protected", "wrong1234"))
                .thenThrow(new ShortUrlPasswordMismatchException("protected"));

        String json = objectMapper.writeValueAsString(Map.of("password", "wrong1234"));

        mockMvc.perform(post("/protected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }
}
