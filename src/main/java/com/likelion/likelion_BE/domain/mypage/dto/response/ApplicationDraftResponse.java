package com.likelion.likelion_BE.domain.mypage.dto.response;

public record ApplicationDraftResponse(
        boolean hasApplication,
        String status,
        Long applicationId,
        String name,
        String part,
        String applicationStatus,
        String updatedAt
) {
    public static ApplicationDraftResponse of(
            Long applicationId,
            String name,
            String part,
            String applicationStatus,
            String updatedAt
    ) {
        return new ApplicationDraftResponse(true, "DRAFT", applicationId, name, part, applicationStatus, updatedAt);
    }

    public static ApplicationDraftResponse empty() {
        return new ApplicationDraftResponse(false, "DRAFT", null, null, null, null, null);
    }
}