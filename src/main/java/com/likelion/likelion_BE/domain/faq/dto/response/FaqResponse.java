package com.likelion.likelion_BE.domain.faq.dto.response;

import com.likelion.likelion_BE.domain.faq.entity.Faq;

import java.time.LocalDateTime;

public record FaqResponse(
        Long recruitmentId,
        Long faqId,
        Long partId,
        String question,
        String answer,
        LocalDateTime createdAt
) {
    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getRecruitment().getId(),
                faq.getRecruitmentPart() != null ? faq.getRecruitmentPart().getId() : null,
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCreatedAt()
        );
    }
}
