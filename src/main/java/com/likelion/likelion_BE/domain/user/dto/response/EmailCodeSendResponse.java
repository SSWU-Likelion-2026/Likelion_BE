package com.likelion.likelion_BE.domain.user.dto.response;

public record EmailCodeSendResponse(
        String email,
        int expiresIn
) {
}
