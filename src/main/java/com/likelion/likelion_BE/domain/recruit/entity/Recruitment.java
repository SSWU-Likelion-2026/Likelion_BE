package com.likelion.likelion_BE.domain.recruit.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminRecruitmentRequest;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
            List<AdminRecruitmentRequest.AdminRecruitmentPartRequest> partRequests
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

        // 파트 업데이트
        updateParts(partRequests);
    }

    private void updateParts(List<AdminRecruitmentRequest.AdminRecruitmentPartRequest> partRequests) {
        if (partRequests == null) return;

        // 요청으로 들어온 기존 파트 Id 목록 추출
        Set<Long> requestPartIds = partRequests.stream()
                .map(AdminRecruitmentRequest.AdminRecruitmentPartRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 삭제된 파트만 컬렉션에서 제거 (기존 파트는 유지됨)
        this.parts.removeIf(part -> !requestPartIds.contains(part.getId()));

        // 기존 파트 수정 또는 신규 파트 생성
        for (AdminRecruitmentRequest.AdminRecruitmentPartRequest request : partRequests) {
            if (request.id() != null) {
                // 기존 파트 -> 객체 참조 유지하며 값만 변경
                this.parts.stream()
                        .filter(part -> part.getId().equals(request.id()))
                        .findFirst()
                        .ifPresent(part -> part.updatePart(request.name(), request.description()));
            } else {
                // 신규 파트 -> 정적 팩토리 메서드로 생성 후 추가
                RecruitmentPart newPart = RecruitmentPart.createPart(request.name(), request.description());
                newPart.assignRecruitment(this);
                this.parts.add(newPart);
            }
        }
    }

    // 모집 공고 상태 변경 메서드
    public void updateStatus(RecruitmentStatus status) {
        this.status = status;
    }

}
