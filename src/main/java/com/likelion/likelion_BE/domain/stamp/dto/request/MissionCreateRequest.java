package com.likelion.likelion_BE.domain.stamp.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record MissionCreateRequest(
        @Valid
        @NotEmpty(message = "등록할 미션 목록이 비어있습니다.")
        List<MissionItemRequest> missions
) {
    public record MissionItemRequest(
            @NotBlank(message = "미션 제목은 필수입니다.")
            String title,

            @NotBlank(message = "미션 설명은 필수입니다.")
            String description,

            @NotNull(message = "기수 기입은 필수입니다.")
            @Positive(message = "기수는 1 이상의 양수여야 합니다.")
            Integer term,

            @NotBlank(message = "이미지 url은 필수입니다.")
            String imageUrl,

            @NotBlank(message = "스탬프 url은 필수입니다.")
            String stampUrl,

            @NotNull(message = "미션 시작일시는 필수입니다.")
            LocalDateTime startAt,

            @NotNull(message = "미션 종료일시는 필수입니다.")
            LocalDateTime endAt
    ) {}
}
