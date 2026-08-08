package com.likelion.likelion_BE.domain.stamp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MissionStatus {
    UPCOMING("진행예정"),
    IN_PROGRESS("진행중"),
    ENDED("종료됨");

    private final String description;

    }
