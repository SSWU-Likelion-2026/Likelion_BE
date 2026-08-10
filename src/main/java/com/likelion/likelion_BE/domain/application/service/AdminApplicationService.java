package com.likelion.likelion_BE.domain.application.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.dto.request.UpdatePassStatusRequest;
import com.likelion.likelion_BE.domain.application.dto.response.AdminApplicationDetailResponse;
import com.likelion.likelion_BE.domain.application.dto.response.AdminApplicationListResponse;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.exception.ApplicationErrorCode;
import com.likelion.likelion_BE.domain.application.repository.ApplicationRepository;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    // 관리자 - 전체 지원서 목록 조회 (파트별, 합불 상태별 필터링 + 페이징)
    public AdminApplicationListResponse getApplications(Integer term, Long partId, PassStatus passStatus, Pageable pageable) {
        Recruitment recruitment = getRecruitmentByTermOrDefault(term);

        Page<Application> applicationPage = applicationRepository.findAllAdminApplications(
                recruitment.getId(),
                partId,
                passStatus,
                pageable
        );

        // 현재 페이지 지원서들의 userId 목록 추출 (중복 제거)
        Set<Long> userIds = applicationPage.getContent().stream()
                .map(Application::getUserId)
                .collect(Collectors.toSet());

        // 유저 정보 일괄 조회 후 Map 변환
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // applicationPage와 userMap을 함께 전달
        return AdminApplicationListResponse.from(applicationPage, userMap);
    }

    // 관리자 - 지원서 상세 조회
    public AdminApplicationDetailResponse getApplicationDetail(Long applicationId) {
        Application application = applicationRepository.findDetailById(applicationId)
                .orElseThrow(() -> new CustomException(ApplicationErrorCode.APPLICATION_NOT_FOUND));

        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        return AdminApplicationDetailResponse.from(application, user);
    }

    // 관리자 - 합불 상태 변경
    @Transactional
    public void updatePassStatus(Long applicationId, UpdatePassStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ApplicationErrorCode.APPLICATION_NOT_FOUND));

        application.updatePassStatus(request.passStatus());
    }

    // 헬퍼 메서드 : term이 있으면 그 기수를, 없다면 최신 기수 공고 가져오기
    private Recruitment getRecruitmentByTermOrDefault(Integer term) {
        if (term != null) {
            return recruitmentRepository.findByTerm(term)
                    .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));
        }
        return recruitmentRepository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));
    }
}
