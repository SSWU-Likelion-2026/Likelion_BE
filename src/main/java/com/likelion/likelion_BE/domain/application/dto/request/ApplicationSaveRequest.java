package com.likelion.likelion_BE.domain.application.dto.request;

import java.util.List;

public record ApplicationSaveRequest(
        Long partId,
        List<AnswerInput> answers
) {
    public record AnswerInput(
            Long questionId,
            String content
    ) {}
}
