package com.ssbaktain.shortly.user.controller;

import com.ssbaktain.shortly.user.domain.User;
import com.ssbaktain.shortly.user.dto.UserResponse;
import com.ssbaktain.shortly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        User user = userService.getById(userId);
        UserResponse response = UserResponse.from(user);
        return ResponseEntity.ok(response);
    }
}
