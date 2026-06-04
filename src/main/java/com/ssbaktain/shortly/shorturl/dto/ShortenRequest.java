package com.ssbaktain.shortly.shorturl.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ShortenRequest {

    @NotBlank(message = "Original URL must not be blank")
    private String originalUrl;

    @Future(message = "expiresAt must be in the future")
    private LocalDateTime expiresAt;

    @Size(min = 4, max = 72, message = "password must be 4 to 72 characters")
    private String password;
}
