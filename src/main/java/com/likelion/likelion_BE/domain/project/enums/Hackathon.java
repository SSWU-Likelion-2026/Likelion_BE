package com.likelion.likelion_BE.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Hackathon {
    IDEATHON("아이디어톤"),
    HERETHON("여기톤"),
    CENTRALTHON("중앙톤");

    private final String name;
}
