package com.likelion.likelion_BE.domain.mypage.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyPageErrorCode implements BaseCode {

    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "MYPAGE4001", "입력값 형식이 올바르지 않습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "MYPAGE4002", "이미지 형식 또는 용량이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
