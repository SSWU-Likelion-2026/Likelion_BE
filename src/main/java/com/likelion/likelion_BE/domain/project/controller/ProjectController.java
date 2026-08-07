package com.likelion.likelion_BE.domain.project.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.project.dto.request.ProjectCreateUpdateRequest;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectCreateUpdateResponse;
import com.likelion.likelion_BE.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project API", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "운영진 권한을 가진 사용자가 새로운 프로젝트를 등록합니다.")
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
}