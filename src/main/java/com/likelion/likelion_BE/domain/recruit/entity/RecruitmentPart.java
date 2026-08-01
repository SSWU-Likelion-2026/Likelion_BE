package com.likelion.likelion_BE.domain.recruit.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruitment_part")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RecruitmentPart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;


    public static RecruitmentPart createPart(String name, String description) {
        return RecruitmentPart.builder()
                .name(name)
                .description(description)
                .build();
    }

    // 연관관계 할당 메서드
    public void assignRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    // 업데이트 메서드
    public void updatePart(String name, String description) {
        this.name = name;
        this.description = description;
    }
}