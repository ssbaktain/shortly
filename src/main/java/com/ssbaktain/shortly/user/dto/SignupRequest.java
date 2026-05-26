package com.ssbaktain.shortly.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 30, message = "Password must be 8-30 characters")
    private String password;

    @NotBlank(message = "Nickname must not be blank")
    @Size(min = 2, max = 20, message = "Nickname must be 2-20 characters")
    private String nickname;
}
