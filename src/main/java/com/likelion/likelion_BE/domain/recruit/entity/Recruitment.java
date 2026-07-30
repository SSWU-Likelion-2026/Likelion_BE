package com.likelion.likelion_BE.domain.recruit.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruitment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status;

    @Column(name = "doc_start_at", nullable = false)
    private LocalDateTime docStartAt;

    @Column(name = "doc_end_at", nullable = false)
    private LocalDateTime docEndAt;

    @Column(name = "doc_result_at", nullable = false)
    private LocalDateTime docResultAt;

    @Column(name = "interview_start_at", nullable = false)
    private LocalDateTime interviewStartAt;

    @Column(name = "interview_end_at", nullable = false)
    private LocalDateTime interviewEndAt;

    @Column(name = "final_result_at", nullable = false)
    private LocalDateTime finalResultAt;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecruitmentPart> parts = new ArrayList<>();

    // 파트 연관관계 메서드
    public void addPart(RecruitmentPart part) {
        this.parts.add(part);
        part.assignRecruitment(this);
    }

    public static Recruitment createRecruitment(
            Integer term,
            String title,
            RecruitmentStatus status,
            LocalDateTime docStartAt,
            LocalDateTime docEndAt,
            LocalDateTime docResultAt,
            LocalDateTime interviewStartAt,
            LocalDateTime interviewEndAt,
            LocalDateTime finalResultAt,
            List<RecruitmentPart> parts
    ) {
        Recruitment recruitment = Recruitment.builder()
                .term(term)
                .title(title)
                .status(status)
                .docStartAt(docStartAt)
                .docEndAt(docEndAt)
                .docResultAt(docResultAt)
                .interviewStartAt(interviewStartAt)
                .interviewEndAt(interviewEndAt)
                .finalResultAt(finalResultAt)
                .build();

        if (parts != null) {
            parts.forEach(recruitment::addPart);
        }

        return recruitment;
    }

    // 모집 공고 수정 메서드
    public void updateRecruitment(
            Integer term,
            String title,
            RecruitmentStatus status,
            LocalDateTime docStartAt,
            LocalDateTime docEndAt,
            LocalDateTime docResultAt,
            LocalDateTime interviewStartAt,
            LocalDateTime interviewEndAt,
            LocalDateTime finalResultAt,
            List<RecruitmentPart> newParts
    ) {
        this.term = term;
        this.title = title;
        this.status = status;
        this.docStartAt = docStartAt;
        this.docEndAt = docEndAt;
        this.docResultAt = docResultAt;
        this.interviewStartAt = interviewStartAt;
        this.interviewEndAt = interviewEndAt;
        this.finalResultAt = finalResultAt;

        // 기존 파트 제거 후 새로운 파트로 교체
        this.parts.clear();
        if (newParts != null) {
            newParts.forEach(this::addPart);
        }
    }

    // 모집 공고 상태 변경 메서드
    public void updateStatus(RecruitmentStatus status) {
        this.status = status;
    }

}
