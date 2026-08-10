package com.likelion.likelion_BE.domain.stamp.dto.response;

import com.likelion.likelion_BE.domain.stamp.entity.Mission;

import java.time.LocalDateTime;

public record MissionListResponse(
        Long missionId,
        String title,
        String description,
        String imageUrl,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean isCompleted
) {
    public static MissionListResponse of(Mission mission, boolean isCompleted) {
        return new MissionListResponse(
                mission.getId(),
                mission.getTitle(),
                mission.getDescription(),
                mission.getImageUrl(),
                mission.getStartAt(),
                mission.getEndAt(),
                isCompleted
        );
    }
}
