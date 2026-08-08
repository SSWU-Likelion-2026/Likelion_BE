package com.likelion.likelion_BE.domain.mypage.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.*;
import com.likelion.likelion_BE.domain.mypage.dto.request.ProfileUpdateRequest;
import com.likelion.likelion_BE.domain.mypage.service.MypageApplicationService;
import com.likelion.likelion_BE.domain.mypage.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Mypage API", description = "마이페이지")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {

    private final MypageService mypageService;
    private final MypageApplicationService mypageApplicationService;

    @Operation(
            summary = "프로필 불러오기",
            description = "")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MypageResponse>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(mypageService.getProfile(userDetails.getUsername())));
    }

    @Operation(
            summary = "프로필 텍스트 정보 수정",
            description = "이름/전공/학번/전화번호를 수정합니다.")
    @PatchMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProfileUpdateResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProfileUpdateResponse response = mypageService.updateProfile(
                userDetails.getUsername(),
                request
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "프로필 이미지 수정",
            description = "프로필 이미지 파일을 교체합니다.")
    @PatchMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileImageUpdateResponse>> updateProfileImage(
            @Parameter(description = "변경할 프로필 이미지 파일 (jpg/png)")
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProfileImageUpdateResponse response = mypageService.updateProfileImage(
                userDetails.getUsername(),
                image
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "지원 현황 조회",
            description = "status=SUBMITTED(지원 완료) 또는 status=DRAFT(임시저장). 유저당 지원서는 최대 1개입니다.")
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<ApplicationListResponse>> getApplication(
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ApplicationListResponse response = mypageApplicationService.getApplication(
                userDetails.getUsername(),
                status
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "임시저장 지원서 불러오기", description = "")
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<ApplicationDetailResponse>> getApplicationDetail(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ApplicationDetailResponse response = mypageApplicationService.getApplicationDetail(
                userDetails.getUsername(),
                applicationId
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "지원서 삭제",
            description = "임시저장 상태의 지원서만 삭제 가능합니다.")
    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<ApplicationDeleteResponse>> deleteApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ApplicationDeleteResponse response = mypageApplicationService.deleteApplication(
                userDetails.getUsername(),
                applicationId
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}