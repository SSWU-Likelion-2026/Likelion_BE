package com.likelion.likelion_BE.domain.recruit.controller;


import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.recruit.dto.response.LandingPageResponse;
import com.likelion.likelion_BE.domain.recruit.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recruitment API", description = "유저 - 모집 랜딩 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @Operation(summary = "랜딩페이지 모집 정보 조회", description = "현재 모집 공고 일정, 파트 목록, FAQ 정보를 한 번에 조회합니다.")
    @GetMapping("/recruitments/landing")
    public ApiResponse<LandingPageResponse> getLandingPageInfo() {
        LandingPageResponse response = recruitmentService.getLandingPageInfo();
        return ApiResponse.onSuccess(response);
    }
}
