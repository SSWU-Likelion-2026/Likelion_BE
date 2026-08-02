package com.likelion.likelion_BE.domain.application.entity;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "application_answer",
        uniqueConstraints = {
                // 한 지원서 내에서 동일한 질문에 대해 답변이 중복 생성되는 것을 방지
                @UniqueConstraint(
                        name = "uk_application_question",
                        columnNames = {"application_id", "question_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ApplicationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private RecruitmentQuestion question;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // 생성 메서드
    public static ApplicationAnswer createAnswer(
            Application application,
            RecruitmentQuestion question,
            String content
    ) {
        ApplicationAnswer answer = ApplicationAnswer.builder()
                .application(application)
                .question(question)
                .content(content)
                .build();

        // 연관관계
        application.getAnswers().add(answer);
        return answer;
    }

    // 답변 내용 수정 메서드 (임시저장/제출 시 사용)
    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
