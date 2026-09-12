package com.likelion.likelion_BE.domain.user.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record UserListResponse (
    Long userId,
    String name,
    String email,
    String role
) {
    public static UserListResponse from(User user) {
        return new UserListResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
