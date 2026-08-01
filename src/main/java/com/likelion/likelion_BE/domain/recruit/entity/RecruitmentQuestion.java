package com.likelion.likelion_BE.domain.recruit.entity;

import com.likelion.likelion_BE.domain.recruit.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "recruitment_question",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recruitment_part_question_number",
                        columnNames = {"recruitment_id", "part_id", "question_number"}
                )
        }
)@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RecruitmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private RecruitmentPart recruitmentPart;

    @Column(name = "question_number", nullable = false)
    private Long questionNumber;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "max_length", nullable = false)
    private Integer maxLength;

    @Column(name = "question_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    public static RecruitmentQuestion createQuestion(
            Recruitment recruitment,
            RecruitmentPart recruitmentPart,
            Long questionNumber,
            String content,
            Integer maxLength,
            QuestionType questionType,
            Boolean isRequired
    ) {
        return RecruitmentQuestion.builder()
                .recruitment(recruitment)
                .recruitmentPart(recruitmentPart)
                .questionNumber(questionNumber)
                .content(content)
                .maxLength(maxLength)
                .questionType(questionType)
                .isRequired(isRequired)
                .build();
    }

    // 수정 더티체킹
    public void updateQuestion(
            RecruitmentPart recruitmentPart,
            Long questionNumber,
            String content,
            Integer maxLength,
            QuestionType questionType,
            Boolean isRequired
    ) {
        this.recruitmentPart = recruitmentPart;
        this.questionNumber = questionNumber;
        this.content = content;
        this.maxLength = maxLength;
        this.questionType = questionType;
        this.isRequired = isRequired;
    }
}
