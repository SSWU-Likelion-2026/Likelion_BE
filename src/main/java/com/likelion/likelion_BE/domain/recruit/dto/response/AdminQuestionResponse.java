package com.likelion.likelion_BE.domain.recruit.dto.response;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import com.likelion.likelion_BE.domain.recruit.enums.QuestionType;

public record AdminQuestionResponse(
        Long questionId,
        Long recruitmentId,
        Long partId, // 공통 질문이면 null
        Long questionNumber,
        String content,
        Integer maxLength,
        QuestionType questionType,
        Boolean isRequired
) {
    public static AdminQuestionResponse from(RecruitmentQuestion question) {
        return new AdminQuestionResponse(
                question.getId(),
                question.getRecruitment().getId(),
                question.getRecruitmentPart() != null ? question.getRecruitmentPart().getId() : null,
                question.getQuestionNumber(),
                question.getContent(),
                question.getMaxLength(),
                question.getQuestionType(),
                question.getIsRequired()
        );
    }
}
