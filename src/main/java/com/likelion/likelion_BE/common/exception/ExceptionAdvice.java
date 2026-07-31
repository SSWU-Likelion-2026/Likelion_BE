package com.likelion.likelion_BE.common.exception;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.common.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // 1. 도메인 커스텀 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(), e.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getErrorCode().getHttpStatus(), webRequest);
    }

    // 2. @Valid @RequestBody DTO 검증 실패 (ResponseEntityExceptionHandler 메서드 오버라이드)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(field, msg, (a, b) -> a + ", " + b);
        });

        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.BAD_REQUEST, errors);
        return handleExceptionInternal(e, body, headers, ErrorCode.BAD_REQUEST.getHttpStatus(), request);
    }

    // 3. @Validated 쿼리 파라미터 / 경로 변수 검증 실패 (첫 번째 코드 적용)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;
            errors.putIfAbsent(fieldName, violation.getMessage());
        });

        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.BAD_REQUEST, errors);
        return handleExceptionInternal(e, body, new HttpHeaders(), ErrorCode.BAD_REQUEST.getHttpStatus(), request);
    }

    // 4. 타입 미스매치, JSON 파싱 에러, IllegalArgumentException 등 자주 발생하는 400 에러 핸들러 추가
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.BAD_REQUEST, e.getMessage(), null);
        return handleExceptionInternal(e, body, new HttpHeaders(), ErrorCode.BAD_REQUEST.getHttpStatus(), request);
    }

    // 5. 알 수 없는 최상위 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        log.error("Unhandled exception: ", e);
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, null);
        return handleExceptionInternal(e, body, new HttpHeaders(), ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), request);
    }
}

