package com.likelion.likelion_BE.domain.project.dto.response;

import java.util.List;

public record ProjectMultiImageUploadResponse(List<String> imageUrls) {
    public static ProjectMultiImageUploadResponse from(List<String> imageUrls) {
        return new ProjectMultiImageUploadResponse(imageUrls);
    }
}