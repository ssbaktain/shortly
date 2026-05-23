package com.ssbaktain.shortly.shorturl.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShortenRequest {

    @NotBlank(message = "Original URL must not be blank")
    private String originalUrl;
}
