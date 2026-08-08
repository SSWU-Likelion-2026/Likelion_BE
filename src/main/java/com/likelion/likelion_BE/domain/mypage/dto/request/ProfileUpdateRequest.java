package com.likelion.likelion_BE.domain.mypage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileUpdateRequest(

        @NotBlank
        @Schema(description = "이름", example = "홍길동")
        String name,

        @NotBlank
        String major,

        @NotBlank
        @Pattern(regexp = "^[0-9]{8,10}$", message = "학번 형식이 올바르지 않습니다.")
        String studentId,

        @NotBlank
        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phoneNumber
) {
}