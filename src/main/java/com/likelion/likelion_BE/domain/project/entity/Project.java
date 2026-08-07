package com.likelion.likelion_BE.domain.project.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.project.enums.Hackathon;
import com.likelion.likelion_BE.domain.project.exception.ProjectErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "project")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    // TODO: User 엔티티 생기면 연결
    // @ManyToOne ..
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Enumerated(EnumType.STRING)
    @Column(name = "hackathon", nullable = false)
    private Hackathon hackathon;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "summary", nullable = false, length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", nullable = false, length = 500)
    private String logoUrl;

    @Column(name = "start_month", nullable = false)
    private LocalDate startMonth;

    @Column(name = "end_month", nullable = false)
    private LocalDate endMonth;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectSlide> slides = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTechStack> techStacks = new ArrayList<>();

    // 컬렉션 캡슐화: 읽기 전용 뷰 반환
    public List<ProjectSlide> getSlides() {
        return Collections.unmodifiableList(slides);
    }

    public List<ProjectMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<ProjectTechStack> getTechStacks() {
        return Collections.unmodifiableList(techStacks);
    }

    // 연관관계 편의 메서드
    public void addSlide(ProjectSlide slide) {
        this.slides.add(slide);
        slide.assignProject(this);
    }

    public void addMember(ProjectMember member) {
        this.members.add(member);
        member.assignProject(this);
    }

    public void addTechStack(ProjectTechStack techStack) {
        this.techStacks.add(techStack);
        techStack.assignProject(this);
    }

    // 정적 팩토리 메서드
    public static Project createProject(
            Long userId,
            Integer term,
            Hackathon hackathon,
            String title,
            String summary,
            String description,
            String logoUrl,
            LocalDate startMonth,
            LocalDate endMonth,
            List<ProjectSlide> slides,
            List<ProjectMember> members,
            List<ProjectTechStack> techStacks
    ) {
        validateProjectPeriod(startMonth, endMonth);

        Project project = Project.builder()
                .userId(userId)
                .term(term)
                .hackathon(hackathon)
                .title(title)
                .summary(summary)
                .description(description)
                .logoUrl(logoUrl)
                .startMonth(startMonth)
                .endMonth(endMonth)
                .build();

        if (slides != null) {
            slides.forEach(project::addSlide);
        }
        if (members != null) {
            members.forEach(project::addMember);
        }
        if (techStacks != null) {
            techStacks.forEach(project::addTechStack);
        }

        return project;
    }

    // 프로젝트 정보 수정
    public void updateProject(
            Integer term,
            Hackathon hackathon,
            String title,
            String summary,
            String description,
            String logoUrl,
            LocalDate startMonth,
            LocalDate endMonth,
            List<ProjectSlide> newSlides,
            List<ProjectMember> newMembers,
            List<ProjectTechStack> newTechStacks
    ) {
        validateProjectPeriod(startMonth, endMonth);

        this.term = term;
        this.hackathon = hackathon;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.logoUrl = logoUrl;
        this.startMonth = startMonth;
        this.endMonth = endMonth;

        this.slides.clear();
        if (newSlides != null) {
            newSlides.forEach(this::addSlide);
        }

        this.members.clear();
        if (newMembers != null) {
            newMembers.forEach(this::addMember);
        }

        this.techStacks.clear();
        if (newTechStacks != null) {
            newTechStacks.forEach(this::addTechStack);
        }
    }

    // 소프트 삭제 (BaseEntity 메서드 사용)
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    private static void validateProjectPeriod(LocalDate startMonth, LocalDate endMonth) {
        // null일 때
        if (startMonth == null || endMonth == null) {
            throw new CustomException(ProjectErrorCode.PROJECT_PERIOD_REQUIRED);
        }

        // 종료일이 시작일보다 빠를 때
        if (endMonth.isBefore(startMonth)) {
            throw new CustomException(ProjectErrorCode.INVALID_PROJECT_PERIOD);
        }
    }
}