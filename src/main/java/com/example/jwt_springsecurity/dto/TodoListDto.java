package com.example.jwt_springsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TodoListDto {

    @NotBlank
    private String title;

    @NotBlank
    private Boolean completed;
}
