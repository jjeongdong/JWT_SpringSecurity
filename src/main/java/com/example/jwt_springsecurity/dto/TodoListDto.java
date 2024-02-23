package com.example.jwt_springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TodoListDto {

    private Long id;

    private String title;

    private Boolean completed;
}
