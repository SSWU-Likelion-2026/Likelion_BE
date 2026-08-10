package com.likelion.likelion_BE.domain.mypage.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicationDetailResponse(
        Long applicationId,
        Long recruitmentId,
        String status,
        Long partId,
        String partName,
        List<AnswerItem> answers,
        LocalDateTime updatedAt
) {

    public record AnswerItem(
            Long questionId,
            String question,
            String content
    ) {
    }
}