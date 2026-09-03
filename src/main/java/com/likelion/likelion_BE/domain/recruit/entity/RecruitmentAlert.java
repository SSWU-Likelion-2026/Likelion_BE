package com.likelion.likelion_BE.domain.recruit.entity;


import com.likelion.likelion_BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruitment_alert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RecruitmentAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    // 공고 일정이 없을 수 있음 nullable = true
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = true)
    private Recruitment recruitment;

    @Column(nullable = false)
    private boolean isSent;

    public static RecruitmentAlert of(String email, Recruitment recruitment, boolean isSent) {
        return RecruitmentAlert.builder()
                .email(email)
                .recruitment(recruitment)
                .isSent(false)
                .build();
    }

    // 발송 완료 처리 메서드
    public void markAsSent() {
        this.isSent = true;
    }



}
