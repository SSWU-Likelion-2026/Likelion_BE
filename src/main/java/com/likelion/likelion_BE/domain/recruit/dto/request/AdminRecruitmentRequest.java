package com.likelion.likelion_BE.domain.recruit.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record AdminRecruitmentRequest(
        @NotNull(message = "기수는 필수 입력값입니다.")
        @Positive(message = "기수는 1 이상의 양수여야 합니다.")
        Integer term,

        @NotNull(message = "제목은 필수 입력값입니다.")
        String title,

        @NotNull(message = "모집 상태는 필수 입력값입니다.")
        RecruitmentStatus status,

        @NotNull(message = "서류 접수 시작 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime docStartAt,

        @NotNull(message = "서류 접수 마감 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime docEndAt,

        @NotNull(message = "서류 결과 발표 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime docResultAt,

        @NotNull(message = "면접 시작 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime interviewStartAt,

        @NotNull(message = "면접 마감 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime interviewEndAt,

        @NotNull(message = "최종 결과 발표 일시는 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime finalResultAt,

        @Valid
        List<RecruitmentPartRequest> parts
) {
        // 날짜 순서 유효성 검증 메서드
        public boolean isValidScheduleSequence() {
                if (docStartAt == null || docEndAt == null || docResultAt == null ||
                        interviewStartAt == null || interviewEndAt == null || finalResultAt == null) {
                        return true;
                }

                return !docEndAt.isBefore(docStartAt)
                        && !docResultAt.isBefore(docEndAt)
                        && !interviewStartAt.isBefore(docResultAt)
                        && !interviewEndAt.isBefore(interviewStartAt)
                        && !finalResultAt.isBefore(interviewEndAt);
        }

        public record RecruitmentPartRequest(
                @NotBlank(message = "파트 이름은 필수 입력 항목입니다.")
                String name,

                String description
        ) {}
}
