package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record AdminApplicationDetailResponse(
        Long applicationId,
        Integer recruitmentTerm,
        ApplicantInfo applicant,
        PartInfo part,
        SubmitStatus submitStatus,
        PassStatus passStatus,
        LocalDateTime submittedAt,
        List<AnswerDetailInfo> answers
) {
    public static AdminApplicationDetailResponse from(Application application, User user) {

        ApplicantInfo applicantInfo = new ApplicantInfo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getMajor(),
                user.getStudentId()
        );

        PartInfo partInfo = application.getRecruitmentPart() != null
                ? new PartInfo(application.getRecruitmentPart().getId(), application.getRecruitmentPart().getName())
                : null;

        List<AnswerDetailInfo> answerInfos = application.getAnswers().stream()
                .map(AnswerDetailInfo::from)
                .toList();

        Integer term = application.getRecruitment() != null ? application.getRecruitment().getTerm() : null;

        return new AdminApplicationDetailResponse(
                application.getId(),
                term,
                applicantInfo,
                partInfo,
                application.getSubmitStatus(),
                application.getPassStatus(),
                application.getUpdatedAt(),
                answerInfos
        );

    }
    // 1. 지원자 정보 내부 DTO
    public record ApplicantInfo(
            Long userId,
            String name,
            String email,
            String phone,
            String department,
            String studentId
    ) {}

    // 2. 파트 정보 내부 DTO
    public record PartInfo(
            Long partId,
            String partName
    ) {}

    // 3. 답변 상세 정보 내부 DTO
    public record AnswerDetailInfo(
            Long questionId,
            Long questionNumber,
            String questionContent,
            String answerContent
    ) {
        public static AnswerDetailInfo from(com.likelion.likelion_BE.domain.application.entity.ApplicationAnswer answer) {
            return new AnswerDetailInfo(
                    answer.getQuestion().getId(),
                    answer.getQuestion().getQuestionNumber(),
                    answer.getQuestion().getContent(),
                    answer.getContent()
            );
        }
    }
}
