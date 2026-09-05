package com.likelion.likelion_BE.domain.project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.likelion_BE.domain.project.entity.Project;
import com.likelion.likelion_BE.domain.project.entity.ProjectMember;
import com.likelion.likelion_BE.domain.project.entity.ProjectSlide;
import com.likelion.likelion_BE.domain.project.enums.Category;
import com.likelion.likelion_BE.domain.project.enums.Hackathon;
import com.likelion.likelion_BE.domain.project.enums.Part;
import lombok.Builder;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record ProjectDetailResponse(
        Long id,
        String logoUrl,
        String title,
        String summary,
        String description,
        Hackathon hackathon,

        @JsonFormat(pattern = "yyyy-MM")
        YearMonth startMonth,

        @JsonFormat(pattern = "yyyy-MM")
        YearMonth endMonth,

        List<String> slideUrls,
        Map<Part, List<String>> membersByPart,            // 파트별 팀원 이름 리스트
        Map<Category, List<TechStackDto>> techStacksByCategory // 카테고리별 기술스택 리스트
) {
    public record TechStackDto(
            Long id,
            String name
    ) {}

    public static ProjectDetailResponse from(Project project) {
        // 1. 슬라이드 순서(sequenceNum) 정렬 후 imageUrl 추출
        List<String> slideUrls = project.getSlides().stream()
                .sorted(Comparator.comparingInt(ProjectSlide::getSequenceNum))
                .map(ProjectSlide::getImageUrl)
                .toList();

        // 2. 팀원 파트(Part)별 그룹화
        Map<Part, List<String>> membersByPart = project.getMembers().stream()
                .collect(Collectors.groupingBy(
                        ProjectMember::getPart,
                        Collectors.mapping(ProjectMember::getName, Collectors.toList())
                ));

        // 3. 기술스택 카테고리(Category)별 그룹화
        Map<Category, List<TechStackDto>> techStacksByCategory = project.getTechStacks().stream()
                .collect(Collectors.groupingBy(
                        pts -> pts.getTechStack().getCategory(),
                        Collectors.mapping(
                                pts -> new TechStackDto(
                                        pts.getTechStack().getId(),
                                        pts.getTechStack().getName()
                                ),
                                Collectors.toList()
                        )
                ));

        return ProjectDetailResponse.builder()
                .id(project.getId())
                .logoUrl(project.getLogoUrl())
                .title(project.getTitle())
                .summary(project.getSummary())
                .description(project.getDescription())
                .hackathon(project.getHackathon())
                .startMonth(YearMonth.from(project.getStartMonth()))
                .endMonth(YearMonth.from(project.getEndMonth()))
                .slideUrls(slideUrls)
                .membersByPart(membersByPart)
                .techStacksByCategory(techStacksByCategory)
                .build();
    }
}