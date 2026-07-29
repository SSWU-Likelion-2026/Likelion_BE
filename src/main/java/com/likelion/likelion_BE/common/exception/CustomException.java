package com.likelion.likelion_BE.common.exception;

import com.likelion.likelion_BE.common.response.BaseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseCode errorCode;

    public CustomException(BaseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(BaseCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}