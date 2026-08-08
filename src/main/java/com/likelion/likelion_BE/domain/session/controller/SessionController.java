package com.likelion.likelion_BE.domain.session.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.project.enums.Part;
import com.likelion.likelion_BE.domain.session.dto.response.SessionDetailResponse;
import com.likelion.likelion_BE.domain.session.dto.response.SessionListResponse;
import com.likelion.likelion_BE.domain.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Session API", description = "세션 관련 API")
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final SessionService sessionService;

    // 세션 목록 조회
    @Operation(summary = "세션 목록 조회", description = "기수(term) 및 파트(part) 기준 세션 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<SessionListResponse>> getSessions(
            @Parameter(description = "기수 필터", example = "14", required = true)
            @RequestParam Integer term,

            @Parameter(description = "파트 필터 (BACKEND, FRONTEND, PM)", example = "PM", required = true)
            @RequestParam Part part
    ) {
        SessionListResponse response = sessionService.getSessions(term, part);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    // 세션 상세 조회
    @Operation(summary = "세션 상세 조회", description = "세션 ID를 통해 세션 상세 정보 및 주요 학습 내용을 조회합니다.")
    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionDetailResponse>> getSessionDetail(
            @Parameter(description = "세션 ID", example = "1", required = true)
            @PathVariable Long sessionId
    ) {
        SessionDetailResponse response = sessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}