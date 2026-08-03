package com.likelion.likelion_BE.domain.faq.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    // null 허용 (part_id가 null이면 해당 기수의 공통 FAQ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_part_id")
    private RecruitmentPart recruitmentPart;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    public static Faq of(Recruitment recruitment, RecruitmentPart recruitmentPart, String question, String answer) {
        return Faq.builder()
                .recruitment(recruitment)
                .recruitmentPart(recruitmentPart)
                .question(question)
                .answer(answer)
                .build();
    }

    // 수정 비즈니스 메서드
    public void update(RecruitmentPart recruitmentPart, String question, String answer) {
        this.recruitmentPart = recruitmentPart;
        this.question = question;
        this.answer = answer;
    }
}
