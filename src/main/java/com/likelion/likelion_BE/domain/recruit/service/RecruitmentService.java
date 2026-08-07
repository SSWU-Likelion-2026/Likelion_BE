package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.domain.recruit.dto.response.CurrentRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final Clock clock;

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
}
