package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;

import java.time.LocalDateTime;
import java.util.List;

public record MyApplicationResponse(
        Long applicationId,
        int recruitmentTerm,
        ApplicantInfo applicant,
        PartInfo part,
        SubmitStatus submitStatus,
        LocalDateTime savedAt,
        LocalDateTime submittedAt,
        List<AnswerDetail> answers
) {
    // 지원자 정보
    public record ApplicantInfo(
            Long userId,
            String name,
            String email,
            String phone,
            String department,
            String studentId
    ) {}

    // 파트 정보
    public record PartInfo(
            Long partId,
            String partName
    ) {}

    // 답변 정보
    public record AnswerDetail(
            Long answerId,
            Long questionId,
            Long questionNumber,
            String questionContent,
            String answerContent
    ) {}

    public static MyApplicationResponse from(Application application) {
        List<AnswerDetail> answerDetails = application.getAnswers().stream()
                .map(answer -> new AnswerDetail(
                        answer.getId(),
                        answer.getQuestion().getId(),
                        answer.getQuestion().getQuestionNumber(),
                        answer.getQuestion().getContent(),
                        answer.getContent()
                ))
                .toList();

        // 파트 정보 변환
        PartInfo partInfo = application.getRecruitmentPart() != null ?
                new PartInfo(application.getRecruitmentPart().getId(), application.getRecruitmentPart().getName()) : null;

        // 지원자 정보
        // TODO: 유저 엔티티 연동 후 User 객체에서 실제 값 추출하도록 변경
        ApplicantInfo applicantInfo = new ApplicantInfo(
                application.getUserId(),
                "김멋사",
                "likelion@sungshin.ac.kr",
                "010-1234-5678",
                "인공지능전공",
                "20231111"
        );

        return new MyApplicationResponse(
                application.getId(),
                application.getRecruitment().getTerm(),
                applicantInfo,
                partInfo,
                application.getSubmitStatus(),
                application.getSavedAt(),
                application.getSubmittedAt(),
                answerDetails
        );
    }
}