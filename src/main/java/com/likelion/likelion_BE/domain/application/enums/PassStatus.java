package com.likelion.likelion_BE.domain.application.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PassStatus {


    PENDING("평가 대기"),
    DOC_PASS("서류 합격"),
    DOC_FAIL("서류 불합격"),
    FINAL_PASS("최종 합격"),
    FINAL_FAIL("최종 불합격");
    private final String description;
}
