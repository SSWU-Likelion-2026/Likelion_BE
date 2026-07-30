package com.likelion.likelion_BE.domain.recruit.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AdminRecruitmentResponse(

    Long recruitmentId,
    Integer term,
    String title,
    RecruitmentStatus status,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime docStartAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime docEndAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime docResultAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime interviewStartAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime interviewEndAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime finalResultAt,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,

    List<RecruitmentPartResponse> parts

    )

    {
        public record RecruitmentPartResponse(
                Long partId,
                String name,
                String description
        ) {
            public static RecruitmentPartResponse from(RecruitmentPart part) {
                return new RecruitmentPartResponse(
                        part.getId(),
                        part.getName(),
                        part.getDescription()
                );
            }
        }

        public static AdminRecruitmentResponse from (Recruitment recruitment){

            List<RecruitmentPartResponse> partResponses = (recruitment.getParts() != null) ?
                    recruitment.getParts().stream()
                            .map(RecruitmentPartResponse::from)
                            .toList() : List.of();
            return new AdminRecruitmentResponse(
                recruitment.getId(),
                recruitment.getTerm(),
                recruitment.getTitle(),
                recruitment.getStatus(),
                recruitment.getDocStartAt(),
                recruitment.getDocEndAt(),
                recruitment.getDocResultAt(),
                recruitment.getInterviewStartAt(),
                recruitment.getInterviewEndAt(),
                recruitment.getFinalResultAt(),
                recruitment.getCreatedAt(),
                partResponses

        );
    }
}
