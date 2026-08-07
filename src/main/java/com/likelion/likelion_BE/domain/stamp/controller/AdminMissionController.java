package com.likelion.likelion_BE.domain.stamp.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.stamp.dto.request.MissionCreateRequest;
import com.likelion.likelion_BE.domain.stamp.dto.request.MissionUpdateRequest;
import com.likelion.likelion_BE.domain.stamp.dto.response.MissionResponse;
import com.likelion.likelion_BE.domain.stamp.service.AdminMissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Stamp Mission API", description = "관리자용 스탬프 미션 CUD API")
@RestController
@RequestMapping("/api/v1/admin/stamps/missions")
@RequiredArgsConstructor
public class AdminMissionController {

    private final AdminMissionService adminMissionService;

    @Operation(summary = "관리자 - 스탬프 미션 생성", description = "새로운 스탬프 미션들을 등록합니다.")
    @PostMapping
    public ApiResponse<List<MissionResponse>> createMissions(@Valid @RequestBody MissionCreateRequest request) {
        return ApiResponse.onSuccess(adminMissionService.createMissions(request));
    }

    @Operation(summary = "관리자 - 스탬프 미션 수정", description = "스탬프 미션 정보를 수정합니다.")
    @PatchMapping("/{missionId}")
    public ApiResponse<MissionResponse> updateMission(
            @PathVariable Long missionId,
            @Valid @RequestBody MissionUpdateRequest request
    ) {
        return ApiResponse.onSuccess(adminMissionService.updateMission(missionId, request));
    }

    @Operation(summary = "관리자 - 스탬프 미션 삭제", description = "스탬프 미션을 삭제합니다.")
    @DeleteMapping("/{missionId}")
    public ApiResponse<Void> deleteMission(@PathVariable Long missionId) {
        adminMissionService.deleteMission(missionId);
        return ApiResponse.onSuccess(null);
    }
}
