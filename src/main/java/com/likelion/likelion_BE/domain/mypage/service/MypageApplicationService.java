package com.likelion.likelion_BE.domain.mypage.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.entity.ApplicationAnswer;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.application.repository.ApplicationRepository;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationDeleteResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationDetailResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationDraftResponse;
import com.likelion.likelion_BE.domain.mypage.dto.response.ApplicationListResponse;
import com.likelion.likelion_BE.domain.mypage.exception.MyPageErrorCode;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageApplicationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;

    public Object getApplication(String email, String statusParam) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        SubmitStatus submitStatus = parseSubmitStatus(statusParam);

        if (submitStatus == SubmitStatus.SUBMITTED) {
            return getSubmittedList(user);
        }
        return getDraft(user);
    }

    private ApplicationListResponse getSubmittedList(User user) {
        List<Application> applications = applicationRepository
                .findByUserIdAndSubmitStatusOrderByIdDesc(user.getId(), SubmitStatus.SUBMITTED);

        List<ApplicationListResponse.ApplicationListItem> items = applications.stream()
                .map(app -> new ApplicationListResponse.ApplicationListItem(
                        app.getId(),
                        user.getName(),
                        app.getRecruitmentPart().getName(),
                        app.getPassStatus().getDescription(),
                        formatDate(app.getSubmittedAt())
                ))
                .toList();

        return ApplicationListResponse.of(SubmitStatus.SUBMITTED.name(), items);
    }

    private ApplicationDraftResponse getDraft(User user) {
        // 현재 OPEN인 모집이 없으면 임시저장 대상 자체가 없음
        Optional<Recruitment> currentRecruitmentOpt =
                recruitmentRepository.findFirstByStatusOrderByCreatedAtDesc(RecruitmentStatus.OPEN);

        if (currentRecruitmentOpt.isEmpty()) {
            return ApplicationDraftResponse.empty();
        }

        Recruitment currentRecruitment = currentRecruitmentOpt.get();

        // (user_id, recruitment_id) 유니크 제약으로 항상 0개 또는 1개
        Optional<Application> applicationOpt = applicationRepository
                .findByUserIdAndRecruitmentId(user.getId(), currentRecruitment.getId());

        // 지원서가 없거나, 있어도 이미 SUBMITTED라면 "임시저장"엔 해당 없음
        if (applicationOpt.isEmpty() || applicationOpt.get().getSubmitStatus() != SubmitStatus.DRAFT) {
            return ApplicationDraftResponse.empty();
        }

        Application application = applicationOpt.get();

        return ApplicationDraftResponse.of(
                application.getId(),
                user.getName(),
                application.getRecruitmentPart().getName(),
                application.getSubmitStatus().getDescription(),
                formatDate(application.getSavedAt())
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

        return new ApplicationDetailResponse(
                application.getId(),
                application.getRecruitment().getId(),
                application.getSubmitStatus().name(),
                application.getRecruitmentPart().getId(),
                application.getRecruitmentPart().getName(),
                application.getAnswers().stream()
                        .map(this::toAnswerItem)
                        .toList(),
                application.getUpdatedAt()
        );
    }

    @Transactional
    public ApplicationDeleteResponse deleteApplication(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        Application application = applicationRepository.findByIdForUpdate(applicationId)
                .orElseThrow(() -> new CustomException(MyPageErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getUserId().equals(user.getId())) {
            throw new CustomException(MyPageErrorCode.NOT_OWN_APPLICATION);
        }

        if (application.getSubmitStatus() == SubmitStatus.SUBMITTED) {
            throw new CustomException(MyPageErrorCode.APPLICATION_ALREADY_SUBMITTED);
        }

        applicationRepository.delete(application);

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