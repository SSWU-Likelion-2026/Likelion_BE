package com.likelion.likelion_BE.domain.recruit.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import com.likelion.likelion_BE.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecruitmentErrorCode implements BaseCode {


    RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUIT404_1", "해당 모집 공고를 찾을 수 없습니다."),
    DUPLICATE_RECRUITMENT_TERM(HttpStatus.CONFLICT, "RECRUIT409_1", "이미 존재하는 기수입니다."),
    INVALID_SCHEDULE_SEQUENCE(HttpStatus.BAD_REQUEST, "RECRUIT400_1", "마감일은 시작일보다 이후여야 합니다."),
    RECRUITMENT_PART_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUITPART404_1", "해당 파트를 찾을 수 없습니다."),
    INVALID_RECRUITMENT_PART(HttpStatus.BAD_REQUEST, "RECRUITPART400_1", "이 파트는 현재 모집 공고에 속한 파트가 아닙니다."),

    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION404_1", "해당 질문을 찾을 수 없습니다."),
    DUPLICATE_QUESTION_NUMBER(HttpStatus.CONFLICT, "QUESTION409_1", "이미 존재하는 문항 순서입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
