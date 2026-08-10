package com.likelion.likelion_BE.domain.session.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SessionErrorCode implements BaseCode {

    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION4001", "존재하지 않거나 삭제된 세션입니다."),
    SESSION_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION4002", "존재하지 않거나 삭제된 세션 후기입니다."),
    SESSION_COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "SESSION4030", "해당 후기를 수정/삭제할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}