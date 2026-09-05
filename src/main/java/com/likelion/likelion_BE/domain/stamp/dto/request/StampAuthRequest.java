package com.likelion.likelion_BE.domain.stamp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record StampAuthRequest(

        @NotNull(message = "인증 이미지는 필수입니다.")
        MultipartFile image,

        @NotNull(message = "인증 날짜는 필수 입력값입니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // YYYY-MM-DD 형식
        LocalDate authDate,

        @NotBlank(message = "인증 소감은 필수 입력값입니다.")
        String content
) {
}
