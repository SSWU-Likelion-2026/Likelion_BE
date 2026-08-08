package com.likelion.likelion_BE.domain.mypage.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

public record ProfileUpdateResponse(
        Long userId,
        String email,
        String major,
        String studentId,
        String phoneNumber
) {
    public static ProfileUpdateResponse of(User user) {
        return new ProfileUpdateResponse(
                user.getId(),
                user.getEmail(),
                user.getMajor(),
                user.getStudentId(),
                user.getPhoneNumber()
        );
    }
}