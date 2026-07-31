package com.likelion.likelion_BE.domain.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    SHORT_ANSWER("단답형"),
    LONG_ANSWER("장문형"),
    FILE("파일 첨부"),
    LINK("링크 첨부");
    private final String description;

}
