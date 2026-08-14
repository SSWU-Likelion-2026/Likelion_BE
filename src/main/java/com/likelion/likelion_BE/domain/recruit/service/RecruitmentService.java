package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.faq.entity.Faq;
import com.likelion.likelion_BE.domain.faq.repository.FaqRepository;
import com.likelion.likelion_BE.domain.recruit.dto.response.CurrentRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.dto.response.LandingPageResponse;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentPartRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final Clock clock;
    private final FaqRepository faqRepository;
    private final RecruitmentPartRepository recruitmentPartRepository;

    public CurrentRecruitmentResponse getCurrentRecruitment() {
        LocalDateTime now = LocalDateTime.now(clock);

        return recruitmentRepository
                .findFirstByStatusAndDocStartAtLessThanEqualAndDocEndAtGreaterThanEqualOrderByTermDesc(
                        RecruitmentStatus.OPEN,
                        now,
                        now
                )
                .map(this::toOpenResponse)
                .orElseGet(CurrentRecruitmentResponse::notification);
    }

    private CurrentRecruitmentResponse toOpenResponse(Recruitment recruitment) {
        LocalDateTime now = LocalDateTime.now(clock);
        long remainingDays = ChronoUnit.DAYS.between(
                now.toLocalDate(),
                recruitment.getDocEndAt().toLocalDate()
        );
        String dDay = "D-" + String.format("%03d", remainingDays);

        return CurrentRecruitmentResponse.open(
                recruitment.getId(),
                recruitment.getTerm(),
                recruitment.getTitle(),
                dDay
        );
    }

    // 랜딩페이지 모집 정보 종합 조회
    public LandingPageResponse getLandingPageInfo() {
        // 1. 현재 OPEN 상태인 모집 공고 조회 (없으면 최신 공고)
        Recruitment recruitment = recruitmentRepository.findFirstByStatusOrderByCreatedAtDesc(RecruitmentStatus.OPEN)
                .orElseGet(() -> recruitmentRepository.findFirstByOrderByCreatedAtDesc()
                        .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND)));

        // 2. 해당 모집 공고의 파트 목록 조회
        List<RecruitmentPart> parts = recruitmentPartRepository.findAllByRecruitmentId(recruitment.getId());

        // 3. 해당 모집 공고의 FAQ 목록 조회
        List<Faq> faqs = faqRepository.findAllByRecruitmentId(recruitment.getId());

        return LandingPageResponse.of(recruitment, parts, faqs);
    }
}
