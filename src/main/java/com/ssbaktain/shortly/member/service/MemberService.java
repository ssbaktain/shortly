package com.ssbaktain.shortly.member.service;

import com.ssbaktain.shortly.member.domain.Member;
import com.ssbaktain.shortly.member.exception.MemberNotFoundException;
import com.ssbaktain.shortly.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
