package com.likelion.likelion_BE.domain.application.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.application.dto.request.UpdatePassStatusRequest;
import com.likelion.likelion_BE.domain.application.dto.response.AdminApplicationDetailResponse;
import com.likelion.likelion_BE.domain.application.dto.response.AdminApplicationListResponse;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.service.AdminApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/admin/applications")
@Tag(name = "Admin Application API", description = "관리자 - 지원서 관련 API")
public class AdminApplicationController {

    private final AdminApplicationService adminApplicationService;

    // 관리자 - 전체 지원서 목록 조회 (파트별, 합불 상태별 필터링 가능)
    @Operation(
            summary = "전체 지원서 목록 조회",
            description = "전체 지원서 목록을 조회합니다. term, partId, passStatus를 선택적으로 입력할 수 있습니다. " +
                    "term을 입력하지 않으면 가장 최신 생성된 term으로 불러옵니다." +
                    "passStatus는 PENDING / DOC_PASS / DOC_FAIL / FINAL_PASS / FINAL_FAIL 가 존재합니다.")
    @GetMapping
    public ApiResponse<AdminApplicationListResponse> getApplications(
            @RequestParam(required = false) Integer term,
            @RequestParam(required = false) Long partId,
            @RequestParam(required = false) PassStatus passStatus,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AdminApplicationListResponse response = adminApplicationService.getApplications(term, partId, passStatus, pageable);
        return ApiResponse.onSuccess(response);
    }

    // 관리자 - 지원서 상세 조회
    @Operation(summary = "지원서 상세 조회", description = "지원서의 id를 입력하면 상세 조회가 가능합니다.")
    @GetMapping("/{applicationId}")
    public ApiResponse<AdminApplicationDetailResponse> getApplicationDetail(
            @PathVariable Long applicationId
    ) {
        AdminApplicationDetailResponse response = adminApplicationService.getApplicationDetail(applicationId);
        return ApiResponse.onSuccess(response);
    }

    // 관리자 - 합불 상태 변경
    @Operation(summary = "합/불 상태 변경", description = "지원서의 id를 입력하면 passStatus를 변경합니다." +
            "passStatus는 PENDING / DOC_PASS / DOC_FAIL / FINAL_PASS / FINAL_FAIL 가 존재합니다.")
    @PatchMapping("/{applicationId}/pass-status")
    public ApiResponse<Void> updatePassStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdatePassStatusRequest request
    ) {
        adminApplicationService.updatePassStatus(applicationId, request);
        return ApiResponse.onSuccess(null);
    }
}
