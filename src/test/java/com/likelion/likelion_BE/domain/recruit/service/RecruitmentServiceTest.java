package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.domain.faq.repository.FaqRepository;
import com.likelion.likelion_BE.domain.recruit.dto.response.CurrentRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentPartRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private RecruitmentPartRepository recruitmentPartRepository;

    @Mock
    private FaqRepository faqRepository;

    private RecruitmentService recruitmentService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-08-07T01:00:00Z"), SEOUL);
        recruitmentService = new RecruitmentService(
                recruitmentRepository,
                clock,
                faqRepository,
                recruitmentPartRepository
        );    }

    @Test
    void 지원기간이면_DDay와_지원하기를_반환한다() {
        Recruitment recruitment = recruitment(
                RecruitmentStatus.OPEN,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                LocalDateTime.of(2026, 8, 14, 23, 59)
        );
        whenCurrentRecruitmentThenReturn(Optional.of(recruitment));

        CurrentRecruitmentResponse response = recruitmentService.getCurrentRecruitment();

        assertThat(response.recruiting()).isTrue();
        assertThat(response.dDay()).isEqualTo("D-007");
        assertThat(response.action()).isEqualTo(CurrentRecruitmentResponse.RecruitmentAction.APPLY);
        assertThat(response.term()).isEqualTo(14);
    }

    @Test
    void 지원기간이_아니면_알림신청을_반환한다() {
        Recruitment recruitment = recruitment(
                RecruitmentStatus.CLOSED,
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 14, 23, 59)
        );
        whenCurrentRecruitmentThenReturn(Optional.empty());

        CurrentRecruitmentResponse response = recruitmentService.getCurrentRecruitment();

        assertThat(response.recruiting()).isFalse();
        assertThat(response.dDay()).isNull();
        assertThat(response.action()).isEqualTo(CurrentRecruitmentResponse.RecruitmentAction.NOTIFICATION);
    }

    @Test
    void 모집공고가_없으면_알림신청을_반환한다() {
        whenCurrentRecruitmentThenReturn(Optional.empty());

        CurrentRecruitmentResponse response = recruitmentService.getCurrentRecruitment();

        assertThat(response.recruiting()).isFalse();
        assertThat(response.action()).isEqualTo(CurrentRecruitmentResponse.RecruitmentAction.NOTIFICATION);
    }

    private Recruitment recruitment(
            RecruitmentStatus status,
            LocalDateTime docStartAt,
            LocalDateTime docEndAt
    ) {
        return Recruitment.createRecruitment(
                14,
                "멋쟁이사자처럼 대학 14기 모집",
                status,
                docStartAt,
                docEndAt,
                docEndAt.plusDays(1),
                docEndAt.plusDays(2),
                docEndAt.plusDays(3),
                docEndAt.plusDays(4),
                List.of()
        );
    }

    private void whenCurrentRecruitmentThenReturn(Optional<Recruitment> recruitment) {
        when(recruitmentRepository
                .findFirstByStatusAndDocStartAtLessThanEqualAndDocEndAtGreaterThanEqualOrderByTermDesc(
                        eq(RecruitmentStatus.OPEN),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(recruitment);
    }
}
