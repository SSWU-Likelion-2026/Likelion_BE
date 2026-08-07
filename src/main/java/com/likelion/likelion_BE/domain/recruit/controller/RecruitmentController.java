package com.likelion.likelion_BE.domain.recruit.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.recruit.dto.response.CurrentRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home API", description = "홈 화면 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @Operation(summary = "홈 화면 모집 상태 조회", description = "홈 화면에 표시할 지원 가능 여부와 마감 D-Day를 조회합니다.")
    @GetMapping("/current")
    public ApiResponse<CurrentRecruitmentResponse> getCurrentRecruitment() {
        return ApiResponse.onSuccess(recruitmentService.getCurrentRecruitment());
    }
}
