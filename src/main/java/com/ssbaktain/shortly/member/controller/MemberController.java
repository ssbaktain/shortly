package com.ssbaktain.shortly.member.controller;

import com.ssbaktain.shortly.member.domain.Member;
import com.ssbaktain.shortly.member.dto.MemberResponse;
import com.ssbaktain.shortly.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/me")
    public ResponseEntity<MemberResponse> getMyInfo(@AuthenticationPrincipal Long memberId) {
        Member member = memberService.getById(memberId);
        MemberResponse response = MemberResponse.from(member);
        return ResponseEntity.ok(response);
    }
}
