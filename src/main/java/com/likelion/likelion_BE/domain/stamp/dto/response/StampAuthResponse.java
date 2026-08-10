package com.likelion.likelion_BE.domain.stamp.dto.response;

import com.likelion.likelion_BE.domain.stamp.entity.UserStamp;

import java.time.LocalDateTime;

public record StampAuthResponse(
        Long userStampId,
        Long missionId,
        String stampImageUrl,
        LocalDateTime attainedAt
) {
    public static StampAuthResponse from(UserStamp userStamp) {
        return new StampAuthResponse(
                userStamp.getId(),
                userStamp.getMission().getId(),
                userStamp.getMission().getStampUrl(),
                userStamp.getCreatedAt()
        );
    }
}
