package com.example.jwt_springsecurity.service;

import com.example.jwt_springsecurity.config.SecurityUtil;
import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.dto.MemberResponseDto;
import com.example.jwt_springsecurity.exception.CustomException;
import com.example.jwt_springsecurity.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDto.of(member);
    }

    public MemberResponseDto findMemberInfoByEmail(String email) {

        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!Objects.equals(member.getEmail(), email)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        Member DBmember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDto.of(DBmember);
    }
}