package com.likelion.likelion_BE.domain.stamp.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum StampErrorCode implements BaseCode {

    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "STAMP404_1", "해당 미션을 찾을 수 없습니다."),
    MISSION_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "STAMP400_1", "종료일시는 시작일시보다 이후여야 합니다."),
    STAMP_MISSION_ALREADY_STAMPED(HttpStatus.BAD_REQUEST, "STAMP400_2", "이미 유저가 스탬프를 획득한 미션은 삭제할 수 없습니다."),
    MISSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "STAMP400_3", "이미 인증을 완료한 미션입니다."),
    MISSION_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "STAMP400_4", "현재 진행 중인 미션 기간이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
