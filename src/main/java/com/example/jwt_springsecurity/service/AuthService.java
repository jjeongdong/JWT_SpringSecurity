package com.example.jwt_springsecurity.service;

import com.example.jwt_springsecurity.config.TokenProvider;
import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.dto.*;
import com.example.jwt_springsecurity.exception.CustomException;
import com.example.jwt_springsecurity.exception.ErrorCode;
import com.example.jwt_springsecurity.redis.RefreshToken;
import com.example.jwt_springsecurity.redis.RefreshTokenRepository;
import com.example.jwt_springsecurity.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        if (memberRepository.existsByUsername(memberRequestDto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public TokenDto login(MemberRequestDto memberRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberRequestDto.getUsername(), memberRequestDto.getPassword());

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Member member = memberRepository.findByUsername(memberRequestDto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // 4. Redis에  RefreshToken 저장
        refreshTokenRepository.save(
                new RefreshToken(refreshToken, member.getId())
        );

        // 5. 토큰 발급
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .build();
    }


    @Transactional
    public TokenDto reissue(RequestTokenDto requestTokenDto) {
        // 1. Redis에 Refresh Token이 저장되어 있는지 확인
        RefreshToken foundTokenInfo = refreshTokenRepository.findById(requestTokenDto.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        Member member = memberRepository.findById(foundTokenInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String refreshToken = foundTokenInfo.getRefreshToken();
        tokenProvider.validateToken(refreshToken);

        // 2. Refresh Token으로 부터 인증 정보를 꺼냄
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        // 3. 새로운 Access Token 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);

        // 4. 새로 발급한 Access Token으로 Redis도 업데이트를 해준다.
        refreshTokenRepository.save(new RefreshToken(refreshToken, member.getId()));

        // Token 재발급
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId())
                .build();
    }

}
