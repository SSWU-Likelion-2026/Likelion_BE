package com.likelion.likelion_BE.domain.mypage.dto.response;

import java.util.List;

public record ApplicationListResponse(
        String status,
        int totalCount,
        List<ApplicationListItem> applications
) {
    public static ApplicationListResponse of(String status, List<ApplicationListItem> items) {
        return new ApplicationListResponse(status, items.size(), items);
    }

    public record ApplicationListItem(
            Long applicationId,
            String name,
            String part,
            String applicationStatus,
            String submittedAt
    ) {
    }
}