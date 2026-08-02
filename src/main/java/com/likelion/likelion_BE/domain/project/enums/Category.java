package com.likelion.likelion_BE.domain.project.enums;

// 기술 스택에 쓰임.

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    PLANNING("기획"),
    DESIGN("디자인"),
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    AI("Ai");

    private final String name;
}
