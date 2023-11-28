package com.example.jwt_springsecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class TodoDto {
    @NotBlank
    private String title;

}
