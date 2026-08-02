package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import com.likelion.likelion_BE.domain.recruit.enums.QuestionType;

import java.util.List;

public record CurrentQuestionsResponse(
        Long recruitmentId,
        int term,
        List<QuestionInfo> commonQuestions,
        List<PartQuestionGroup> partQuestions
) {

    public record QuestionInfo(
            Long questionId,
            Long questionNumber,
            String content,
            QuestionType questionType,
            Integer maxLength,
            boolean isRequired
    ){
        public static QuestionInfo from(RecruitmentQuestion question) {
            return new QuestionInfo(
                    question.getId(),
                    question.getQuestionNumber(),
                    question.getContent(),
                    question.getQuestionType(),
                    question.getMaxLength(),
                    Boolean.TRUE.equals(question.getIsRequired())
            );
        }
    }

    public record PartQuestionGroup(
            Long partId,
            String partName,
            List<QuestionInfo> questions
    ){}
}
