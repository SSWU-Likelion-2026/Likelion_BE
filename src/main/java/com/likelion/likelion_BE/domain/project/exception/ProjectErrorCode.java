package com.likelion.likelion_BE.domain.project.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCode implements BaseCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4040", "존재하지 않거나 이미 삭제된 프로젝트입니다."),
    PROJECT_FORBIDDEN_CREATE(HttpStatus.FORBIDDEN, "PROJECT4030", "프로젝트를 생성할 권한이 없습니다."),
    PROJECT_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "PROJECT4031", "프로젝트를 수정할 권한이 없습니다."),
    PROJECT_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "PROJECT4032", "프로젝트를 삭제할 권한이 없습니다."),
    TECH_STACK_NOT_FOUND(HttpStatus.BAD_REQUEST, "PROJECT4000", "존재하지 않는 기술 스택이 포함되어 있습니다."),
    PROJECT_PERIOD_REQUIRED(HttpStatus.BAD_REQUEST, "PROJECT4001", "프로젝트 기간은 필수 항목입니다."),
    INVALID_PROJECT_PERIOD(HttpStatus.BAD_REQUEST, "PROJECT4002", "종료일은 시작일보다 빠를 수 없습니다."),

    EXCEEDED_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "PROJECT4003", "장표 이미지는 최대 10개까지 업로드 가능합니다."),
    EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "PROJECT4004", "업로드할 이미지 파일이 없습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "PROJECT4005", "JPG 또는 PNG 형식의 이미지만 업로드 가능합니다."),
    EXCEEDED_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "PROJECT4006", "이미지 파일 크기는 개별 10MB를 초과할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}