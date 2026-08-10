package com.likelion.likelion_BE.domain.mypage.dto.response;

public record ProfileImageUpdateResponse(
        String profileImageUrl
) {
    public static ProfileImageUpdateResponse of(String profileImageUrl) {
        return new ProfileImageUpdateResponse(profileImageUrl);
    }
}