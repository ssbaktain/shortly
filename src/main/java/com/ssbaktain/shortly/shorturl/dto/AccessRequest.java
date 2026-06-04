package com.ssbaktain.shortly.shorturl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessRequest {

    @NotBlank(message = "password must not be blank")
    @Size(min = 4, max = 72, message = "password must be 4 to 72 characters")
    private String password;
}
