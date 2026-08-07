package com.likelion.likelion_BE.domain.faq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FaqUpdateRequest(
        Long partId, // null 가능 (공통 FAQ로 변경시 null)

        @NotBlank(message = "질문 내용은 필수입니다.")
        String question,

        @NotBlank(message = "답변 내용은 필수입니다.")
        String answer
) {
}
