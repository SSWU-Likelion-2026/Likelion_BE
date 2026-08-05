package com.likelion.likelion_BE.domain.user.dto.response;

public record EmailVerificationResponse(
        String email,
        boolean verified
) {
}
