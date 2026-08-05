package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record SignupResponse(
        Long userId,
        String name,
        String email,
        String role
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}