package com.likelion.likelion_BE.domain.user.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {


    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4001","비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH4002", "이메일 인증이 완료되지 않았습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH4003", "인증번호가 올바르지 않습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH4004", "인증번호가 만료되었습니다. 다시 요청해주세요."),
    NOT_SUNGSHIN_EMAIL(HttpStatus.BAD_REQUEST, "AUTH4005", "성신 이메일이 아닙니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER4091", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH4010", "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "유효하지 않은 Refresh Token입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH4012", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH4030", "접근 권한이 없습니다."),
    TOO_MANY_VERIFICATION_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "AUTH4291", "인증번호 발송 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
