package com.likelion.likelion_BE.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "Google ID Token은 필수입니다.")
        String idToken
) {
}