package com.likelion.likelion_BE.domain.faq.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum FaqErrorCode implements BaseCode {
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "FAQ404_1", "해당 FAQ를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
