package com.likelion.likelion_BE.domain.mypage.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.entity.ApplicationAnswer;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.application.repository.ApplicationRepository;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationDeleteResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationDetailResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationListResponse;
import com.likelion.likelion_BE.domain.mypage.exception.MyPageErrorCode;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageApplicationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationListResponse getApplication(String email, String statusParam) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        SubmitStatus submitStatus = parseSubmitStatus(statusParam);

        Optional<Application> applicationOpt =
                applicationRepository.findByUserIdAndSubmitStatus(user.getId(), submitStatus);

        if (applicationOpt.isEmpty()) {
            return ApplicationListResponse.empty(submitStatus.name());
        }

        Application application = applicationOpt.get();

        String submittedAt = null;
        String updatedAt = null;
        String applicationStatus;

        if (submitStatus == SubmitStatus.SUBMITTED) {
            submittedAt = formatDate(application.getSubmittedAt());
            applicationStatus = application.getPassStatus().getDescription();
        } else {
            updatedAt = formatDate(application.getSavedAt());
            applicationStatus = application.getSubmitStatus().getDescription();
        }

        return ApplicationListResponse.of(
                submitStatus.name(),
                application.getId(),
                user.getName(),
                application.getRecruitmentPart().getName(), // 필드명 추정, 확인 필요
                applicationStatus,
                submittedAt,
                updatedAt
        );
    }

    public ApplicationDetailResponse getApplicationDetail(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(MyPageErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUserId().equals(user.getId())) {
            throw new CustomException(MyPageErrorCode.NOT_OWN_APPLICATION);
        }

        if(application.getSubmitStatus() == SubmitStatus.SUBMITTED) {
            throw new CustomException(MyPageErrorCode.APPLICATION_ALREADY_SUBMITTED);
        }

        return new ApplicationDetailResponse(
                application.getId(),
                application.getRecruitment().getId(),
                application.getSubmitStatus().name(),
                application.getRecruitmentPart().getId(),
                application.getRecruitmentPart().getName(), // 필드명 추정, 확인 필요
                application.getAnswers().stream()
                        .map(this::toAnswerItem)
                        .toList(),
                application.getUpdatedAt() // BaseEntity 필드명 추정, 확인 필요
        );
    }

    @Transactional
    public ApplicationDeleteResponse deleteApplication(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(MyPageErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUserId().equals(user.getId())) {
            throw new CustomException(MyPageErrorCode.NOT_OWN_APPLICATION);
        }

        int deletedCount = applicationRepository.deleteByIdAndUserIdAndSubmitStatus(
                applicationId,
                user.getId(),
                SubmitStatus.DRAFT
        );

        if (deletedCount == 0) {
            throw new CustomException(MyPageErrorCode.APPLICATION_ALREADY_SUBMITTED);
        }

        return ApplicationDeleteResponse.of(applicationId);
    }

    private ApplicationDetailResponse.AnswerItem toAnswerItem(ApplicationAnswer answer) {
        return new ApplicationDetailResponse.AnswerItem(
                answer.getQuestion().getId(),
                answer.getQuestion().getContent(),
                answer.getContent()
        );
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_FORMATTER);
    }

    private SubmitStatus parseSubmitStatus(String statusParam) {
        try {
            return SubmitStatus.valueOf(statusParam);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(MyPageErrorCode.INVALID_QUERY_STATUS);
        }
    }
}