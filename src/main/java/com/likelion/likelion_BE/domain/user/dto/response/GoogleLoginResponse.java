package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record GoogleLoginResponse(
        Long userId,
        String name,
        String role,
        String accessToken,
        String refreshToken,
        boolean isNewUser
) {
    public static GoogleLoginResponse of(
            User user,
            TokenRefreshResponse tokenResponse,
            boolean isNewUser
    ) {
        return new GoogleLoginResponse(
                user.getId(),
                user.getName(),
                user.getRole().name(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                isNewUser
        );
    }
}