package com.likelion.likelion_BE.domain.project.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProjectErrorCode implements BaseCode {

    // 400 BAD_REQUEST
    INVALID_PROJECT_SLIDE_COUNT(HttpStatus.BAD_REQUEST, "PROJECT400_1", "프로젝트 장표는 최대 10개까지 등록 가능합니다."),
    INVALID_PROJECT_TECH_STACK(HttpStatus.BAD_REQUEST, "PROJECT400_2", "유효하지 않은 기술 스택이 포함되어 있습니다."),
    INVALID_PROJECT_MEMBER(HttpStatus.BAD_REQUEST, "PROJECT400_3", "프로젝트 팀원 정보가 유효하지 않습니다."),
    PROJECT_PERIOD_REQUIRED(HttpStatus.BAD_REQUEST, "PROJECT400_4", "프로젝트 시작일과 종료일은 필수 입력값입니다."),
    INVALID_PROJECT_PERIOD(HttpStatus.BAD_REQUEST, "PROJECT400_5", "종료일은 시작일보다 이전일 수 없습니다."),


    // 403 FORBIDDEN
    PROJECT_FORBIDDEN_CREATE(HttpStatus.FORBIDDEN, "PROJECT403_1", "프로젝트 등록 권한이 없습니다."),
    PROJECT_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "PROJECT403_2", "프로젝트 수정 권한이 없습니다."),
    PROJECT_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "PROJECT403_3", "프로젝트 삭제 권한이 없습니다."),

    // 404 NOT_FOUND
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_1", "해당 프로젝트를 찾을 수 없습니다."),
    TECH_STACK_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404_2", "존재하지 않는 기술 스택입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}