package com.likelion.likelion_BE.domain.application.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.application.exception.ApplicationErrorCode;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "application",
        uniqueConstraints = {
                // 한 유저가 동일 기수 모집에 중복 지원하는 것을 막기
                @UniqueConstraint(name = "uk_user_recruitment", columnNames = {"user_id", "recruitment_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private RecruitmentPart recruitmentPart;

    @Enumerated(EnumType.STRING)
    @Column(name = "submit_status", nullable = false, length = 20)
    private SubmitStatus submitStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "pass_status", nullable = false, length = 20)
    private PassStatus passStatus;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "user_id")
    private Long userId;

    @Builder.Default
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAnswer> answers = new ArrayList<>();


    // 생성 메서드
    public static Application createApplication(
            Recruitment recruitment,
            RecruitmentPart recruitmentPart,
            Long userId
    ) {
        return Application.builder()
                .recruitment(recruitment)
                .recruitmentPart(recruitmentPart)
                .userId(userId)
                .submitStatus(SubmitStatus.DRAFT)      // 최초 생성 시 임시저장 상태
                .passStatus(PassStatus.PENDING)        // 최초 생성 시 평가 대기 상태
                .savedAt(LocalDateTime.now())
                .build();
    }

    // 임시 저장 업데이트 메서드
    public void updateDraft(RecruitmentPart part) {
        this.recruitmentPart = part;
        this.submitStatus = SubmitStatus.DRAFT;
        this.savedAt = LocalDateTime.now();
    }

    // 최종 제출
    public void submit(RecruitmentPart part) {
        this.recruitmentPart = part;
        this.submitStatus = SubmitStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    // 관리자 - 지원서 합불 상태 변경
    public void updatePassStatus(PassStatus newPassStatus) {
        if (newPassStatus == null) {
            throw new CustomException(ApplicationErrorCode.INVALID_PASS_STATUS);
        }
        this.passStatus = newPassStatus;
    }

}
