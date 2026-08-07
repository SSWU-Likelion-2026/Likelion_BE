package com.likelion.likelion_BE.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.likelion_BE.domain.project.entity.Project;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectCreateUpdateResponse(
        Long projectId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
    public static ProjectCreateUpdateResponse from(Project project) {
        return ProjectCreateUpdateResponse.builder()
                .projectId(project.getId())
                .createdAt(project.getCreatedAt())
                .build();
    }
}