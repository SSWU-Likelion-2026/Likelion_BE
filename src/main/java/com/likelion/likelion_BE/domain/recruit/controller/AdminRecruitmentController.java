package com.likelion.likelion_BE.domain.recruit.controller;


import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminRecruitmentRequest;
import com.likelion.likelion_BE.domain.recruit.dto.response.AdminRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.service.AdminRecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/recruitments")
public class AdminRecruitmentController {

    private final AdminRecruitmentService adminRecruitmentService;

    // 모집 공고 생성
    @Operation(summary = "모집 공고 생성", description = "새로운 기수의 모집 공고 및 모집 파트를 등록합니다.")
    @PostMapping
    public ApiResponse<AdminRecruitmentResponse> createRecruitment(
            @Valid @RequestBody AdminRecruitmentRequest request
    ) {
        AdminRecruitmentResponse response = adminRecruitmentService.createRecruitment(request);
        return ApiResponse.onSuccess(response);
    }

    // 모집 공고 수정
    @Operation(summary = "모집 공고 수정", description = "기존 모집 공고의 일정, 상태, 모집 파트 정보를 수정합니다.")
    @PutMapping("/{recruitmentId}")
    public ApiResponse<AdminRecruitmentResponse> updateRecruitment(
            @PathVariable Long recruitmentId,
            @Valid @RequestBody AdminRecruitmentRequest request
    ) {
        AdminRecruitmentResponse response = adminRecruitmentService.updateRecruitment(recruitmentId, request);
        return ApiResponse.onSuccess(response);
    }
}
