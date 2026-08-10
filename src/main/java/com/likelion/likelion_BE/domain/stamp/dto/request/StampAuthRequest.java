package com.likelion.likelion_BE.domain.stamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record StampAuthRequest(

        @NotNull(message = "인증 날짜는 필수 입력값입니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // YYYY-MM-DD 형식
        LocalDate authDate,

        @NotBlank(message = "인증 소감은 필수 입력값입니다.")
        String content
) {
}
