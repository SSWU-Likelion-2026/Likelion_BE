package com.likelion.likelion_BE.domain.recruit.dto.request;

import com.likelion.likelion_BE.domain.recruit.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdminQuestionRequest(

        Long partId, // null이면 공통 질문

        @NotNull(message = "문항 순서는 필수입니다.")
        @Positive(message = "문항 순서는 양수여야 합니다.")
        Long questionNumber,

        @NotBlank(message = "질문 내용은 필수입니다.")
        String content,

        @NotNull(message = "글자수 제한은 필수입니다.")
        Integer maxLength,

        @NotNull(message = "질문 유형은 필수입니다.")
        QuestionType questionType,

        @NotNull(message = "필수 여부는 필수입니다.")
        Boolean isRequired
) {
}
