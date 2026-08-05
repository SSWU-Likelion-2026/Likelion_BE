package com.likelion.likelion_BE.domain.user.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.user.dto.request.*;
import com.likelion.likelion_BE.domain.user.dto.response.EmailCodeSendResponse;
import com.likelion.likelion_BE.domain.user.dto.response.EmailVerificationResponse;
import com.likelion.likelion_BE.domain.user.dto.response.TokenRefreshResponse;
import com.likelion.likelion_BE.domain.user.dto.response.UserResponse;
import com.likelion.likelion_BE.domain.user.service.EmailVerificationService;
import com.likelion.likelion_BE.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "유저 - 회원가입/로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "로컬 회원가입",
            description = "")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(userService.signup(request)));
    }

    @Operation(
            summary = "로컬 로그인",
            description = "")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(userService.login(request)));
    }

    @Operation(
            summary = "accessToken 재발급",
            description = "")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> reissue(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(userService.reissue(request)));
    }

    @Operation(
            summary = "로그아웃",
            description = "")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        userService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "이메일 인증코드 전송",
            description = "")
    @PostMapping("/email/verification-code")
    public ResponseEntity<ApiResponse<EmailCodeSendResponse>> sendVerificationCode(
            @Valid @RequestBody EmailCodeSendRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(emailVerificationService.sendVerificationCode(request))
        );
    }

    @Operation(
            summary = "이메일 인증코드 확인",
            description = "")
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerificationResponse>> verifyCode(
            @Valid @RequestBody EmailVerificationRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(emailVerificationService.verifyCode(request))
        );
    }
}
