package com.likelion.likelion_BE.domain.stamp.dto.response;

import com.likelion.likelion_BE.domain.stamp.entity.Mission;
import com.likelion.likelion_BE.domain.stamp.enums.MissionStatus;

import java.time.LocalDateTime;

public record MissionResponse(
        Long id,
        String title,
        String description,
        Integer term,
        String imageUrl,
        String stampUrl,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        MissionStatus status

) {
    public static MissionResponse from(Mission mission) {

        LocalDateTime now = LocalDateTime.now();
        MissionStatus status;

        if (now.isBefore(mission.getStartAt())) {
            status = MissionStatus.UPCOMING;
        } else if (now.isAfter(mission.getEndAt())) {
            status = MissionStatus.ENDED;
        } else {
            status = MissionStatus.IN_PROGRESS;
        }

        return new MissionResponse(
                mission.getId(),
                mission.getTitle(),
                mission.getDescription(),
                mission.getTerm(),
                mission.getImageUrl(),
                mission.getStampUrl(),
                mission.getStartAt(),
                mission.getEndAt(),
                mission.getCreatedAt(),
                status
        );
    }
}
