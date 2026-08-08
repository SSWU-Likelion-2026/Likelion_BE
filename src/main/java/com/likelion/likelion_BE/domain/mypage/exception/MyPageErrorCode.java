package com.likelion.likelion_BE.domain.mypage.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyPageErrorCode implements BaseCode {

    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "MYPAGE4001", "입력값 형식이 올바르지 않습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "MYPAGE4002", "이미지 형식 또는 용량이 올바르지 않습니다."),
    INVALID_QUERY_STATUS(HttpStatus.BAD_REQUEST, "MYPAGE4003", "유효하지 않은 조회 상태값입니다."),

    APPLICATION_ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "APPLICATION4004", "이미 제출된 지원서입니다."),
    NOT_OWN_APPLICATION(HttpStatus.FORBIDDEN, "APPLICATION4031", "본인의 지원서만 조회할 수 있습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION4041", "존재하지 않는 지원서입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
