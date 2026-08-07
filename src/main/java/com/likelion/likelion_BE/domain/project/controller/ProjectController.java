package com.likelion.likelion_BE.domain.project.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.project.dto.request.ProjectCreateUpdateRequest;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectCreateUpdateResponse;
import com.likelion.likelion_BE.domain.project.dto.response.RecentProjectResponse;
import com.likelion.likelion_BE.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project API", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated // @RequestParam의 @Min, @Max 검증 활성화를 위해 필요
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "운영진 권한(LEADER, MANAGER)을 가진 사용자가 새로운 프로젝트를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectCreateUpdateResponse>> createProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProjectCreateUpdateRequest request
    ) {
        ProjectCreateUpdateResponse response = projectService.createProject(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "프로젝트 수정", description = "운영진 권한(LEADER, MANAGER)을 가진 사용자가 기존 프로젝트 정보를 전체 수정합니다.")
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectCreateUpdateResponse>> updateProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProjectCreateUpdateRequest request
    ) {
        ProjectCreateUpdateResponse response = projectService.updateProject(projectId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "프로젝트 삭제", description = "운영진 권한(LEADER, MANAGER)을 가진 사용자가 프로젝트를 삭제 처리합니다.")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        projectService.deleteProject(projectId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // HOME 조회
    @Operation(summary = "최근 프로젝트 조회", description = "홈 화면에 표시할 프로젝트를 최신 등록 순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RecentProjectResponse>>> getRecentProjects(
            @Parameter(description = "조회 개수 (기본 3개, 최대 10개)", example = "3")
            @RequestParam(defaultValue = "3")
            @Min(value = 1, message = "조회 개수는 1개 이상이어야 합니다.")
            @Max(value = 10, message = "조회 개수는 최대 10개까지 가능합니다.")
            int size
    ) {
        List<RecentProjectResponse> response = projectService.getRecentProjects(size);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}