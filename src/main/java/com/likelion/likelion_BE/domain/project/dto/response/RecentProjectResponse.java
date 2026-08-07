package com.likelion.likelion_BE.domain.project.dto.response;

import com.likelion.likelion_BE.domain.project.entity.Project;

public record RecentProjectResponse(
        Long projectId,
        String title,
        String summary,
        String thumbnailUrl
) {
    public static RecentProjectResponse from(Project project) {
        return new RecentProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getSummary(),
                project.getLogoUrl()
        );
    }
}
