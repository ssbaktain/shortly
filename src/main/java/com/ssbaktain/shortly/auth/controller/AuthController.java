package com.ssbaktain.shortly.auth.controller;

import com.ssbaktain.shortly.auth.service.AuthService;
import com.ssbaktain.shortly.member.domain.Member;
import com.ssbaktain.shortly.member.dto.LoginRequest;
import com.ssbaktain.shortly.member.dto.SignupRequest;
import com.ssbaktain.shortly.member.dto.TokenResponse;
import com.ssbaktain.shortly.member.dto.MemberResponse;
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

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody SignupRequest request) {
        Member member = authService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        MemberResponse response = MemberResponse.from(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String accessToken = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        TokenResponse response = TokenResponse.of(accessToken);
        return ResponseEntity.ok(response);
    }
}
