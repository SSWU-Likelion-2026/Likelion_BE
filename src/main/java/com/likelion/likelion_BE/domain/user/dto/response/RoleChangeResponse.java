package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record RoleChangeResponse(
        Long userId,
        String name,
        String role
) {
    public static RoleChangeResponse of(User user) {
        return new RoleChangeResponse(
                user.getId(),
                user.getName(),
                user.getRole().name()
        );
    }
}