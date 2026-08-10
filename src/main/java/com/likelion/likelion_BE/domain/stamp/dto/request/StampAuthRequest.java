package com.likelion.likelion_BE.domain.stamp.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record StampAuthRequest(
        @NotBlank(message = "인증 소감은 필수 입력값입니다.")
        String content
) {
}
