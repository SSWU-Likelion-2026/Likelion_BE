package com.likelion.likelion_BE.domain.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
    UPCOMING("모집 예정"),
    OPEN("모집 중"),
    CLOSED("모집 마감");

    private final String description;

}
