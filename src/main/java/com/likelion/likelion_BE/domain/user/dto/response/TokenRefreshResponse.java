package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record TokenRefreshResponse (
    String accessToken,
    String refreshToken,
    long accessTokenExpiresIn
){
    public static TokenRefreshResponse of(
            String accessToken,
            String refreshToken,
            long accessTokenExpiresIn
    ) {
        return new TokenRefreshResponse(
                accessToken,
                refreshToken,
                accessTokenExpiresIn
        );
    }
}

