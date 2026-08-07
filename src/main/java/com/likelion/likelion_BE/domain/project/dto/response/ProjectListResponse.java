package com.likelion.likelion_BE.domain.project.dto.response;

import com.likelion.likelion_BE.domain.project.entity.Project;
import lombok.Builder;

@Builder
public record ProjectListResponse(
        Long id,
        String title,
        String summary,
        String logoUrl
) {
    public static ProjectListResponse from(Project project) {
        return ProjectListResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .summary(project.getSummary())
                .logoUrl(project.getLogoUrl())
                .build();
    }
}