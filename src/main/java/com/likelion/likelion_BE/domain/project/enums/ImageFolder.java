package com.likelion.likelion_BE.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 저장할 디렉토리를 우선 지정.

@Getter
@RequiredArgsConstructor
public enum ImageFolder {
    LOGO("projects/logos"),
    SLIDE("projects/slides");

    private final String path;
}