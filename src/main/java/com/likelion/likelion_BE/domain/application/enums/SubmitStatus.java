package com.likelion.likelion_BE.domain.application.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SubmitStatus {

    DRAFT("임시저장"),
    SUBMITTED("제출완료");

    private final String description;
}
