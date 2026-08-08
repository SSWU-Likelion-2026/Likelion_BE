package com.likelion.likelion_BE.domain.mypage.dto.response;

public record ApplicationDeleteResponse(
        Long applicationId
) {
    public static ApplicationDeleteResponse of(Long applicationId) {
        return new ApplicationDeleteResponse(applicationId);
    }
}