package com.example.jwt_springsecurity.service;

import com.example.jwt_springsecurity.config.TokenProvider;
import com.example.jwt_springsecurity.domain.Member;
import com.example.jwt_springsecurity.domain.RefreshToken;
import com.example.jwt_springsecurity.dto.*;
import com.example.jwt_springsecurity.exception.CustomException;
import com.example.jwt_springsecurity.exception.ErrorCode;
import com.example.jwt_springsecurity.repository.MemberRepository;
import com.example.jwt_springsecurity.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
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

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // 4. RefreshToken 저장
        refreshTokenRepository.save(RefreshToken.builder().value(refreshToken).build());

        // 5. 토큰 발급
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenDto reissue(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByValue(refreshTokenDto.getRefreshToken())
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        Authentication authentication = tokenProvider.getAuthentication(refreshToken.getValue());

        String accessToken = tokenProvider.generateAccessToken(authentication);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .build();
    }
}
