package com.likelion.likelion_BE.domain.memberprofile.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberProfileErrorCode implements BaseCode {

    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "로그인이 필요한 요청입니다."),
    MEMBER_PROFILE_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER_PROFILE403_1", "해당 기수의 최종 합격자 또는 운영진만 프로필을 등록, 수정, 삭제할 수 있습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "사용자를 찾을 수 없습니다."),
    MEMBER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_PROFILE404_1", "부원 프로필을 찾을 수 없습니다."),
    MEMBER_PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_PROFILE409_1", "해당 기수의 부원 프로필이 이미 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
