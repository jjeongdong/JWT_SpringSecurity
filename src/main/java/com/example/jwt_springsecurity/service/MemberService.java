package com.example.jwt_springsecurity.service;

import com.example.jwt_springsecurity.config.SecurityUtil;
import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.dto.MemberResponseDto;
import com.example.jwt_springsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponseDto findMemberInfoById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("유저 정보가 일치하지 않습니다."));

        return MemberResponseDto.of(member);
    }

    public MemberResponseDto findMemberInfoByEmail(String email) {

        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

        if (!Objects.equals(member.getEmail(), email)) {
            throw new RuntimeException("권한이 존재하지 않습니다.");
        }

        Member DBmember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 정보가 일치하지 않습니다."));

        return MemberResponseDto.of(DBmember);
    }
}