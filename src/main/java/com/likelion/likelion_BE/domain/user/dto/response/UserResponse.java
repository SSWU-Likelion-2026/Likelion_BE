package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record UserResponse(
        Long userId,
        String name,
        String email,
        String role,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {
    public static UserResponse of(
            User user,
            String accessToken,
            String refreshToken,
            long accessTokenExpiresIn
    ) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                refreshToken,
                accessTokenExpiresIn
        );
    }
}
