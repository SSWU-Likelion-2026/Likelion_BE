package com.likelion.likelion_BE.domain.application.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements BaseCode {


    PART_REQUIRED(HttpStatus.BAD_REQUEST, "APPLICATION400_1", "지원할 파트를 선택해 주세요."),
    ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "APPLICATION400_2", "이미 제출이 완료된 지원서입니다."),
    REQUIRED_QUESTION_MISSING(HttpStatus.BAD_REQUEST, "APPLICATION400_3", "필수 질문에 대한 답변을 모두 작성해 주세요."),
    ANSWER_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "APPLICATION400_4", "답변 글자 수가 제한을 초과했습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION404_1", "지원서를 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
