package com.likelion.likelion_BE.domain.faq.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FaqRequest(

        @NotNull(message = "모집 공고 ID(recruitmentId)는 필수입니다.")
        Long recruitmentId,

        @Valid
        @NotEmpty(message = "등록할 FAQ 목록이 비어있습니다.")
        List<FaqItemRequest> faqs
        ) {

        public record FaqItemRequest(
                Long partId, // null 가능

                @NotBlank(message = "질문 내용은 필수입니다.")
                String question,

                @NotBlank(message = "답변 내용은 필수입니다.")
                String answer
        ) {}
}
