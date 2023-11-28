package com.example.jwt_springsecurity.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class PageResponseDto {

    private List<TodoListDto> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

}