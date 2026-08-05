package com.likelion.likelion_BE.domain.user.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {


    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4001","비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    NOT_SUNGSHIN_EMAIL(HttpStatus.BAD_REQUEST, "AUTH4003", "성신 이메일이 아닙니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER4091", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4010", "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "유효하지 않은 Refresh Token입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH4012", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH4030", "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
