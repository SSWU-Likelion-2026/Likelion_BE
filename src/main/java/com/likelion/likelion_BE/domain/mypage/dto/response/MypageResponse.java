package com.likelion.likelion_BE.domain.mypage.dto.response;

import com.likelion.likelion_BE.domain.user.entity.User;

import java.time.format.DateTimeFormatter;

public record MypageResponse(
        Long userId,
        String name,
        String profileImageUrl,
        String greeting,
        String email,
        String major,
        String studentId,
        String phoneNumber,
        String joinedAt,
        String role
) {

    private static final DateTimeFormatter JOINED_AT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String GREETING_TEMPLATE = "성신 멋사 사이트 방문을 환영해요!";

    public static MypageResponse of(User user) {
        return new MypageResponse(
                user.getId(),
                user.getName(),
                user.getProfileImageUrl(),
                GREETING_TEMPLATE,
                user.getEmail(),
                user.getMajor(),
                user.getStudentId(),
                user.getPhoneNumber(),
                user.getCreatedAt().format(JOINED_AT_FORMATTER),
                user.getRole().name()
        );
    }
}
