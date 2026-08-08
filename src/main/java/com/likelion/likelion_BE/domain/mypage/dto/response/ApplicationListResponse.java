package com.likelion.likelion_BE.domain.mypage.dto.response;

import java.util.List;

public record ApplicationListResponse(
        boolean hasApplication,
        String status,
        Long applicationId,
        String name,
        String part,
        String applicationStatus,
        String submittedAt,
        String updatedAt
) {

    public static ApplicationListResponse of(
            String status,
            Long applicationId,
            String name,
            String part,
            String applicationStatus,
            String submittedAt,
            String updatedAt
    ) {
        return new ApplicationListResponse(
                true,
                status,
                applicationId,
                name,
                part,
                applicationStatus,
                submittedAt,
                updatedAt
        );
    }

    public static ApplicationListResponse empty(String status) {
        return new ApplicationListResponse(false, status, null, null, null, null, null, null);
    }
}
