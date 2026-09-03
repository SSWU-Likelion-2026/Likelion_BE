package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.domain.recruit.dto.request.RecruitmentAlertRequest;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentAlert;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentAlertRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentAlertService {

    private final RecruitmentAlertRepository recruitmentAlertRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public void registerAlert(RecruitmentAlertRequest request) {

        // 이미 대기 중인 이메일이면 중복 등록 없이 조용히 종료
        if (recruitmentAlertRepository.existsByEmailAndIsSentFalse(request.email())) {
            return;
        }

        // UPCOMING 공고 조회 (공고 등록 전이면 null)
        Recruitment upcomingRecruitment = recruitmentRepository
                .findFirstByStatusOrderByCreatedAtDesc(RecruitmentStatus.UPCOMING)
                .orElse(null);

        recruitmentAlertRepository.save(RecruitmentAlert.of(request.email(), upcomingRecruitment, false));

    }
}
