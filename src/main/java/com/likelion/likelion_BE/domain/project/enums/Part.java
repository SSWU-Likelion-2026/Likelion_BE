package com.likelion.likelion_BE.domain.project.enums;

// 프로젝트 팀원에 쓰임.

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Part {
    PM("기획/디자인"),
    FRONTEND("프론트엔드"),
    BACKEND("백엔드");

    private final String name;
}
