package com.ssbaktain.shortly.auth.controller;

import com.ssbaktain.shortly.user.domain.User;
import com.ssbaktain.shortly.user.dto.SignupRequest;
import com.ssbaktain.shortly.user.dto.UserResponse;
import com.ssbaktain.shortly.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        UserResponse response = UserResponse.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
