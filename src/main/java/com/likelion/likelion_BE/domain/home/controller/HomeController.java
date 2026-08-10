package com.likelion.likelion_BE.domain.home.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.project.dto.response.RecentProjectResponse;
import com.likelion.likelion_BE.domain.project.service.ProjectService;
import com.likelion.likelion_BE.domain.recruit.dto.response.CurrentRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Home API", description = "홈 화면 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Validated
public class HomeController {

    private final RecruitmentService recruitmentService;
    private final ProjectService projectService;

    @Operation(summary = "현재 모집 정보 조회", description = "홈 화면에 표시할 지원 가능 여부와 마감 D-Day를 조회합니다.")
    @GetMapping("/recruitments/current")
    public ApiResponse<CurrentRecruitmentResponse> getCurrentRecruitment() {
        return ApiResponse.onSuccess(recruitmentService.getCurrentRecruitment());
    }

    @Operation(summary = "최근 프로젝트 조회", description = "홈 화면에 표시할 프로젝트를 최신 등록 순으로 조회합니다.")
    @GetMapping("/projects")
    public ApiResponse<List<RecentProjectResponse>> getRecentProjects(
            @Parameter(description = "조회 개수 (기본 3개, 최대 10개)", example = "3")
            @RequestParam(defaultValue = "3")
            @Min(value = 1, message = "조회 개수는 1개 이상이어야 합니다.")
            @Max(value = 10, message = "조회 개수는 최대 10개까지 가능합니다.")
            int size
    ) {
        return ApiResponse.onSuccess(projectService.getRecentProjects(size));
    }
}
