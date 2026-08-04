package com.likelion.likelion_BE.domain.memberprofile.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileCreateRequest;
import com.likelion.likelion_BE.domain.memberprofile.dto.request.MemberProfileUpdateRequest;
import com.likelion.likelion_BE.domain.memberprofile.dto.response.MemberProfileDetailResponse;
import com.likelion.likelion_BE.domain.memberprofile.dto.response.MemberProfileListResponse;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberGroup;
import com.likelion.likelion_BE.domain.memberprofile.enums.MemberType;
import com.likelion.likelion_BE.domain.memberprofile.service.MemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Member Profile API", description = "부원 프로필 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member-profiles")
@Validated
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    @Operation(summary = "부원 목록 조회", description = "기수와 선택 필터로 부원 프로필 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<MemberProfileListResponse>> getProfiles(
            @RequestParam @Positive(message = "기수는 1 이상의 양수여야 합니다.") Integer term,
            @RequestParam(required = false) MemberGroup memberGroup,
            @RequestParam(required = false) MemberType memberType
    ) {
        return ApiResponse.onSuccess(memberProfileService.getProfiles(term, memberGroup, memberType));
    }

    @Operation(summary = "내 프로필 등록", description = "현재 사용자의 기수별 부원 프로필을 등록합니다.")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileDetailResponse>> createMyProfile(
            Principal principal,
            @Valid @RequestBody MemberProfileCreateRequest request
    ) {
        MemberProfileDetailResponse response = memberProfileService.createMyProfile(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "내 프로필 조회", description = "현재 사용자의 특정 기수 프로필을 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<MemberProfileDetailResponse> getMyProfile(
            Principal principal,
            @RequestParam @Positive(message = "기수는 1 이상의 양수여야 합니다.") Integer term
    ) {
        return ApiResponse.onSuccess(memberProfileService.getMyProfile(principal, term));
    }

    @Operation(summary = "내 프로필 수정", description = "현재 사용자의 특정 기수 프로필을 부분 수정합니다.")
    @PatchMapping("/me")
    public ApiResponse<MemberProfileDetailResponse> updateMyProfile(
            Principal principal,
            @RequestParam @Positive(message = "기수는 1 이상의 양수여야 합니다.") Integer term,
            @Valid @RequestBody MemberProfileUpdateRequest request
    ) {
        return ApiResponse.onSuccess(memberProfileService.updateMyProfile(principal, term, request));
    }

    @Operation(summary = "내 프로필 삭제", description = "현재 사용자의 특정 기수 프로필을 삭제합니다.")
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMyProfile(
            Principal principal,
            @RequestParam @Positive(message = "기수는 1 이상의 양수여야 합니다.") Integer term
    ) {
        memberProfileService.deleteMyProfile(principal, term);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "부원 상세 조회", description = "프로필 ID로 부원 프로필을 조회합니다.")
    @GetMapping("/{profileId}")
    public ApiResponse<MemberProfileDetailResponse> getProfile(
            @PathVariable @Positive(message = "프로필 ID는 양수여야 합니다.") Long profileId
    ) {
        return ApiResponse.onSuccess(memberProfileService.getProfile(profileId));
    }
}
