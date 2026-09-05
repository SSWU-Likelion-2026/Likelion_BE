package com.likelion.likelion_BE.domain.project.dto.response;

// 단건 업로드

public record ProjectImageUploadResponse(String imageUrl) {
    public static ProjectImageUploadResponse from(String imageUrl) {
        return new ProjectImageUploadResponse(imageUrl);
    }
}