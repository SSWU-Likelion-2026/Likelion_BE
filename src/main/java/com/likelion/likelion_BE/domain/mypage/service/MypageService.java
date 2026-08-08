package com.likelion.likelion_BE.domain.mypage.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.mypage.dto.request.ProfileUpdateRequest;
import com.likelion.likelion_BE.domain.mypage.dto.response.MypageResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ProfileImageUpdateResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ProfileUpdateResponse;
import com.likelion.likelion_BE.domain.mypage.exception.MyPageErrorCode;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import com.likelion.likelion_BE.domain.user.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MypageService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB, 가정치 — 실제 정책값 확인 필요

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public MypageResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        return MypageResponse.of(user);
    }

    @Transactional
    public ProfileUpdateResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        user.updateProfile(
                request.name(),
                request.major(),
                request.studentId(),
                request.phoneNumber()
        );

        return ProfileUpdateResponse.of(user);
    }

    @Transactional
    public ProfileImageUpdateResponse updateProfileImage(String email, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(MyPageErrorCode.INVALID_IMAGE_FILE);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new CustomException(MyPageErrorCode.INVALID_IMAGE_FILE);
        }

        try {
            String uploadedUrl = s3Service.upload(image);
            user.updateProfileImageUrl(uploadedUrl);
            return ProfileImageUpdateResponse.of(uploadedUrl);
        } catch (IllegalArgumentException e) {
            throw new CustomException(MyPageErrorCode.INVALID_IMAGE_FILE);
        }
    }
}
