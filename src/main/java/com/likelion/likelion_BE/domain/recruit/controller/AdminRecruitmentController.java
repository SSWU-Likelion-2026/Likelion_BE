package com.likelion.likelion_BE.domain.recruit.controller;


import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminQuestionRequest;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminRecruitmentRequest;
import com.likelion.likelion_BE.domain.recruit.dto.response.AdminQuestionResponse;
import com.likelion.likelion_BE.domain.recruit.dto.response.AdminRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.service.AdminQuestionService;
import com.likelion.likelion_BE.domain.recruit.service.AdminRecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Recruitment API", description = "관리자 - 모집 공고 및 질문 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Validated
public class AdminRecruitmentController {

    private final AdminRecruitmentService adminRecruitmentService;
    private final AdminQuestionService adminQuestionService;

    // 모집 공고 생성
    @Operation(summary = "모집 공고 생성", description = "새로운 기수의 모집 공고 및 모집 파트를 등록합니다.")
    @PostMapping("/recruitments")
    public ApiResponse<AdminRecruitmentResponse> createRecruitment(
            @Valid @RequestBody AdminRecruitmentRequest request
    ) {
        AdminRecruitmentResponse response = adminRecruitmentService.createRecruitment(request);
        return ApiResponse.onSuccess(response);
    }

    // 모집 공고 수정
    @Operation(summary = "모집 공고 수정", description = "기존 모집 공고의 일정, 상태, 모집 파트 정보를 수정합니다.")
    @PutMapping("/recruitments/{recruitmentId}")
    public ApiResponse<AdminRecruitmentResponse> updateRecruitment(
            @PathVariable Long recruitmentId,
            @Valid @RequestBody AdminRecruitmentRequest request
    ) {
        AdminRecruitmentResponse response = adminRecruitmentService.updateRecruitment(recruitmentId, request);
        return ApiResponse.onSuccess(response);
    }

    // 모집 질문 일괄 등록
    @Operation(summary = "모집 질문 일괄 등록", description = "모집 공고에 속한 질문 목록을 일괄 등록합니다.")
    @PostMapping("/recruitments/{recruitmentId}/questions")
    public ApiResponse<List<AdminQuestionResponse>> createQuestion(
            @PathVariable Long recruitmentId,
            @Valid @RequestBody List<AdminQuestionRequest> requests
    ) {
        List<AdminQuestionResponse> responses = adminQuestionService.createQuestions(recruitmentId, requests);
        return ApiResponse.onSuccess(responses);
    }

    // 모집 질문 수정
    @Operation(summary = "모집 질문 수정", description = "기존 모집 질문의 내용, 파트, 순서 등을 수정합니다.")
    @PutMapping("/questions/{questionId}")
    public ApiResponse<AdminQuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody AdminQuestionRequest request
    ) {
        AdminQuestionResponse response = adminQuestionService.updateQuestion(questionId, request);
        return ApiResponse.onSuccess(response);
    }

    // 모집 질문 삭제
    @Operation(summary = "모집 질문 삭제", description = "특정 모집 질문을 삭제합니다.")
    @DeleteMapping("/questions/{questionId}")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long questionId
    ) {
        adminQuestionService.deleteQuestion(questionId);
        return ApiResponse.onSuccess(null);
    }
}
