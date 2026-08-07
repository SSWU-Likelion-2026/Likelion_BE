package com.likelion.likelion_BE.domain.recruit.dto.response;

public record CurrentRecruitmentResponse(
        Long recruitmentId,
        Integer term,
        String title,
        boolean recruiting,
        String dDay,
        RecruitmentAction action
) {
    public enum RecruitmentAction {
        APPLY,
        NOTIFICATION
    }

    public static CurrentRecruitmentResponse open(
            Long recruitmentId,
            Integer term,
            String title,
            String dDay
    ) {
        return new CurrentRecruitmentResponse(
                recruitmentId,
                term,
                title,
                true,
                dDay,
                RecruitmentAction.APPLY
        );
    }

    public static CurrentRecruitmentResponse notification() {
        return new CurrentRecruitmentResponse(
                null,
                null,
                null,
                false,
                null,
                RecruitmentAction.NOTIFICATION
        );
    }
}
