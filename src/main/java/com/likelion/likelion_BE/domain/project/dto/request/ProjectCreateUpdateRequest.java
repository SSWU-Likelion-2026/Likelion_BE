package com.likelion.likelion_BE.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.likelion.likelion_BE.domain.project.enums.Hackathon;
import com.likelion.likelion_BE.domain.project.enums.Part;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.YearMonth;
import java.util.List;

public record ProjectCreateUpdateRequest(
        @NotNull(message = "기수는 필수 선택 항목입니다.")
        Integer term,

        @NotNull(message = "해커톤 종류는 필수 선택 항목입니다.")
        Hackathon hackathon,

        @NotBlank(message = "프로젝트 이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "프로젝트 이름은 100자 이하이어야 합니다.")
        String title,

        @NotBlank(message = "프로젝트 슬로건은 필수 입력 항목입니다.")
        @Size(max = 200, message = "프로젝트 요약은 200자 이하이어야 합니다.")
        String summary,

        @NotBlank(message = "프로젝트 상세 설명은 필수 입력 항목입니다.")
        String description,

        @NotNull(message = "시작일은 필수 입력 항목입니다.")
        @JsonFormat(pattern = "yyyy-MM")
        @JsonDeserialize(using = YearMonthDeserializer.class)
        YearMonth startMonth,

        @NotNull(message = "종료일은 필수 입력 항목입니다.")
        @JsonFormat(pattern = "yyyy-MM")
        @JsonDeserialize(using = YearMonthDeserializer.class)
        YearMonth endMonth,

        @NotBlank(message = "로고 이미지는 필수 입력 항목입니다.")
        @org.hibernate.validator.constraints.URL(message = "올바른 로고 URL 형식이 아닙니다.")
        @Size(max = 500, message = "로고 URL은 최대 500자까지 입력 가능합니다.")
        String logoUrl,

        @NotEmpty(message = "장표 이미지는 최소 1개 이상 등록해야 합니다.")
        @Size(max = 10, message = "장표 이미지는 최대 10개까지 추가 가능합니다.")
        List<
                @NotBlank(message = "장표 이미지 URL은 필수입니다.")
                @org.hibernate.validator.constraints.URL(message = "올바른 장표 URL 형식이 아닙니다.")
                @Size(max = 500, message = "장표 이미지 URL은 최대 500자까지 입력 가능합니다.")
                String
        > slideUrls,

        @NotEmpty(message = "프로젝트 팀원 정보는 필수 입력 항목입니다.")
        @Valid
        List<
                @NotNull(message = "팀원 정보는 null일 수 없습니다.")
                ProjectMemberRequest> members,

        @NotEmpty(message = "기술 스택은 최소 1개 이상 선택해야 합니다.")
        List<
                @NotNull(message = "기술 스택 ID는 null일 수 없습니다.")
                        Long
                > techStackIds
) {
    public record ProjectMemberRequest(
            @NotBlank(message = "팀원 이름은 필수 입력 항목입니다.")
            String name,
            @NotNull(message = "팀원 파트는 필수 입력 항목입니다.")
            Part part
    ) {}
}