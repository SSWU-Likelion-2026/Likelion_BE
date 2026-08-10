package com.likelion.likelion_BE.domain.stamp.dto.response;

import com.likelion.likelion_BE.domain.stamp.entity.UserStamp;

import java.time.LocalDate;
import java.util.List;

public record MyStampResponse(
        String userName,
        int totalStampCount,
        List<StampItem> stamps
) {
    public record StampItem(
            Long userStampId,
            Long missionId,
            String missionTitle,
            String stampImageUrl,
            LocalDate authDate
    ) {
        public static StampItem from(UserStamp userStamp) {
            return new StampItem(
                    userStamp.getId(),
                    userStamp.getMission().getId(),
                    userStamp.getMission().getTitle(),
                    userStamp.getMission().getStampUrl(),
                    userStamp.getAuthDate() != null ? userStamp.getAuthDate() : userStamp.getCreatedAt().toLocalDate()
            );
        }
    }

    public static MyStampResponse of(String userName, List<UserStamp> userStamps) {
        List<StampItem> items = userStamps.stream()
                .map(StampItem::from)
                .toList();

        return new MyStampResponse(userName, items.size(), items);
    }
}
