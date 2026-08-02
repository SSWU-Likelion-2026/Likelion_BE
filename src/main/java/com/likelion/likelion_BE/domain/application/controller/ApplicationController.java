package com.likelion.likelion_BE.domain.application.controller;


import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.application.dto.request.ApplicationSaveRequest;
import com.likelion.likelion_BE.domain.application.dto.response.CurrentQuestionsResponse;
import com.likelion.likelion_BE.domain.application.dto.response.MyApplicationResponse;
import com.likelion.likelion_BE.domain.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Application API", description = "유저 - 지원서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class ApplicationController {

    private final ApplicationService applicationService;

    // 지원서 질문 목록 조회
    @Operation(summary = "지원서 질문 목록 조회", description = "현재 진행 중인 모집 공고의 공통 및 파트별 질문 목록을 조회합니다.")
    @GetMapping("/recruitments/current/questions")
    public ApiResponse<CurrentQuestionsResponse> getCurrentQuestions() {
        CurrentQuestionsResponse response = applicationService.getCurrentQuestions();
        return ApiResponse.onSuccess(response);
    }

    // 내 지원서 조회
    @Operation(summary = "내 지원서 조회", description = "현재 진행 중인 모집 공고에 작성 중이거나 제출한 내 지원서를 조회합니다.")
    @GetMapping("/applications/me")
    public ApiResponse<MyApplicationResponse> getMyApplication(
            @RequestParam(defaultValue = "1") Long userId
    ) {
        MyApplicationResponse response = applicationService.getMyApplication(userId);
        return ApiResponse.onSuccess(response);
    }

    // 지원서 임시저장
    @Operation(summary = "지원서 임시저장", description = "작성 중인 지원서를 임시 저장합니다. (필수 항목 미입력 허용)")
    @PostMapping("/applications/draft")
    public ApiResponse<Long> saveDraft(
            @RequestParam(defaultValue = "1") Long userId,
            @Valid @RequestBody ApplicationSaveRequest request
    ) {
        Long applicationId = applicationService.saveDraft(userId, request);
        return ApiResponse.onSuccess(applicationId);
    }

    // 지원서 최종 제출
    @Operation(summary = "지원서 최종 제출", description = "지원서를 최종 제출합니다. (필수 답변 및 글자 수 검증)")
    @PostMapping("/applications/submit")
    public ApiResponse<Long> submitApplication(
            @RequestParam(defaultValue = "1") Long userId,
            @Valid @RequestBody ApplicationSaveRequest request
    ) {
        Long applicationId = applicationService.submitApplication(userId, request);
        return ApiResponse.onSuccess(applicationId);
    }
}
