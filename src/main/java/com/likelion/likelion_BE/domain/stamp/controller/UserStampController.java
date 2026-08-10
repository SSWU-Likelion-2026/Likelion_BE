package com.likelion.likelion_BE.domain.stamp.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.stamp.dto.request.StampAuthRequest;
import com.likelion.likelion_BE.domain.stamp.dto.response.MissionListResponse;
import com.likelion.likelion_BE.domain.stamp.dto.response.MyStampResponse;
import com.likelion.likelion_BE.domain.stamp.dto.response.StampAuthResponse;
import com.likelion.likelion_BE.domain.stamp.service.UserStampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "User Stamp API", description = "유저용 스탬프/미션 API")
@RestController
@RequestMapping("/api/v1/stamps")
@RequiredArgsConstructor
public class UserStampController {

    private final UserStampService userStampService;

    @Operation(summary = "스탬프 미션 목록 조회", description = "기수 미입력 시 DB의 최신 기수 미션을 조회합니다.")
    @GetMapping("/missions")
    public ApiResponse<List<MissionListResponse>> getMissions(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) Integer term
    ) {
        List<MissionListResponse> response = userStampService.getMissions(userId, term);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "스탬프 미션 인증 (사진 업로드 + 소감 작성)")
    @PostMapping(value = "/missions/{missionId}/auth", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StampAuthResponse> authenticateMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionId,
            @RequestPart("image") MultipartFile image,
            @Valid @ModelAttribute StampAuthRequest request
    ) {
        StampAuthResponse response = userStampService.authenticateMission(userId, missionId, image, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "내 스탬프 목록 조회")
    @GetMapping("/me")
    public ApiResponse<MyStampResponse> getMyStamps(
            @AuthenticationPrincipal Long userId
    ) {
        MyStampResponse response = userStampService.getMyStamps(userId);
        return ApiResponse.onSuccess(response);
    }
}