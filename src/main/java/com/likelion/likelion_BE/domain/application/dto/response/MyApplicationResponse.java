package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.user.entity.User;

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

    public static MyApplicationResponse from(Application application, User user) {
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
        ApplicantInfo applicantInfo = new ApplicantInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getMajor(),
                user.getStudentId()
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