package com.example.jwt_springsecurity.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class RequestTokenDto {
    @NotEmpty
    String refreshToken;
}
