package com.example.jwt_springsecurity.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 존재하지 않습니다."),
    TODO_NOT_FOUND("투두가 존재하지 않습니다."),
    DUPLICATE_USER_ID("이미 사용 중인 회원 아이디입니다."),
    WRONG_PASSWORD("비밀번호가 틀렸습니다."),
    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다.");

    private final String message;

    public String getStatus(){
        return name();
    }

    public String getMessage(){
        return message;
    }
}
