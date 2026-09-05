package com.likelion.likelion_BE.domain.project.controller;

import com.likelion.likelion_BE.common.response.ApiResponse;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectImageUploadResponse;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectMultiImageUploadResponse;
import com.likelion.likelion_BE.domain.project.enums.ImageFolder;
import com.likelion.likelion_BE.domain.project.exception.ProjectErrorCode; // 프로젝트 내 Exception 및 ErrorCode
import com.likelion.likelion_BE.common.exception.CustomException; // 프로젝트 공통 CustomException
import com.likelion.likelion_BE.domain.user.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Tag(name = "Project API", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/api/v1/projects/images")
@RequiredArgsConstructor
public class ProjectImageController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (Byte)
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg");

    private final S3Service s3Service;

    // 1. 단건 이미지 업로드 (로고용)
    @Operation(summary = "프로젝트 이미지 단건 업로드", description = "프로젝트 로고 이미지를 업로드하고 S3 URL을 반환받습니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectImageUploadResponse>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일 (JPG, PNG만 가능, 10MB 이하)")
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "이미지 저장 폴더 타입 (LOGO: projects/logos)", example = "LOGO")
            @RequestParam("type") ImageFolder type
    ) {
        // 단건 검증 실행
        validateSingleImage(file);

        String imageUrl = s3Service.upload(file, type);
        return ResponseEntity.ok(ApiResponse.onSuccess(ProjectImageUploadResponse.from(imageUrl)));
    }

    // 2. 다건 이미지 업로드 (장표용 - 최대 10개)
    @Operation(summary = "프로젝트 이미지 다건 업로드", description = "프로젝트 발표 장표 이미지를 다건(최대 10개) 업로드하고 S3 URL 목록을 반환받습니다.")
    @PostMapping(value = "/upload/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProjectMultiImageUploadResponse>> uploadImages(
            @Parameter(description = "업로드할 이미지 파일 목록 (최대 10개, JPG/PNG만 가능, 개별 10MB 이하)")
            @RequestPart("files") List<MultipartFile> files,

            @Parameter(description = "이미지 저장 폴더 타입 (SLIDE: projects/slides)", example = "SLIDE")
            @RequestParam("type") ImageFolder type
    ) {
        // 다건 검증 실행
        validateBulkImages(files);

        List<String> imageUrls = files.stream()
                .map(file -> s3Service.upload(file, type))
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(ProjectMultiImageUploadResponse.from(imageUrls)));
    }

    // === 이미지 검증 전용 Private 메서드 ===

    private void validateSingleImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ProjectErrorCode.EMPTY_IMAGE_FILE);
        }
        validateImageFile(file);
    }

    private void validateBulkImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(ProjectErrorCode.EMPTY_IMAGE_FILE);
        }

        // 1. 개수 제한 검증 (최대 10개)
        if (files.size() > 10) {
            throw new CustomException(ProjectErrorCode.EXCEEDED_IMAGE_COUNT);
        }

        // 2. 각 파일별 용량 및 확장자 검증
        for (MultipartFile file : files) {
            validateImageFile(file);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ProjectErrorCode.EMPTY_IMAGE_FILE);
        }

        // 용량 검증 (10MB 제한)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ProjectErrorCode.EXCEEDED_IMAGE_SIZE);
        }

        // 확장자 검증 (JPG, PNG만 허용)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new CustomException(ProjectErrorCode.INVALID_IMAGE_TYPE);
        }
    }
}