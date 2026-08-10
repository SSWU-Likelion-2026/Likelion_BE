package com.likelion.likelion_BE.domain.session.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.session.dto.request.SessionCommentCreateUpdateRequest;
import com.likelion.likelion_BE.domain.session.dto.response.SessionCommentResponse;
import com.likelion.likelion_BE.domain.session.service.SessionCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Session API", description = "세션 관련 API")
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Validated
public class SessionCommentController {

    private final SessionCommentService commentService;

    @Operation(summary = "세션 후기 목록 조회", description = "해당 세션에 작성된 삭제되지 않은 후기 목록을 조회합니다.")
    @GetMapping("/{sessionId}/comments")
    public ResponseEntity<ApiResponse<List<SessionCommentResponse>>> getComments(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        List<SessionCommentResponse> response = commentService.getComments(sessionId, email);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "세션 후기 작성", description = "부원(MEMBER, LEADER, MANAGER) 권한을 가진 사용자가 후기를 작성합니다.")
    @PostMapping("/{sessionId}/comments")
    public ResponseEntity<ApiResponse<SessionCommentResponse>> createComment(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SessionCommentCreateUpdateRequest request
    ) {
        SessionCommentResponse response = commentService.createComment(sessionId, userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @Operation(summary = "세션 후기 수정", description = "본인이 작성한 세션 후기를 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<SessionCommentResponse>> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SessionCommentCreateUpdateRequest request
    ) {
        SessionCommentResponse response = commentService.updateComment(commentId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "세션 후기 삭제", description = "본인이 작성한 세션 후기를 삭제(Soft Delete)합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}