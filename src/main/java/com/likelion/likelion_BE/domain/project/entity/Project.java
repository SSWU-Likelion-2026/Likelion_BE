package com.likelion.likelion_BE.domain.project.entity;

import com.likelion.likelion_BE.common.entity.BaseEntity;
import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.project.enums.Hackathon;
import com.likelion.likelion_BE.domain.project.exception.ProjectErrorCode;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.enums.Role;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public List<ProjectSlide> getSlides() {
        return Collections.unmodifiableList(slides);
    }

    public List<ProjectMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<ProjectTechStack> getTechStacks() {
        return Collections.unmodifiableList(techStacks);
    }

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

    public static Project createProject(
            User user,
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
                .user(user)
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

    // 작성자 본인 및 직책(LEADER/MANAGER) 검증 메서드
    public void validateOwnerAndAdminRole(User requester, ProjectErrorCode errorCode) {
        // 1. 작성자 본인 여부 확인
        boolean isOwner = this.user.getId().equals(requester.getId());
        if (!isOwner) {
            throw new CustomException(errorCode);
        }

        // 2. LEADER 또는 MANAGER 직책 보유 여부 확인
        Role role = requester.getRole();
        if (role != Role.LEADER && role != Role.MANAGER) {
            throw new CustomException(errorCode);
        }
    }

    /**
     * 순서(sequenceNum)가 1번인 장표의 URL을 추출하여 썸네일로 반환.
     * 등록된 장표가 없을 경우 logoUrl을 기본값으로 반환.
     */
    public String getThumbnailUrl() {
        return this.slides.stream()
                .filter(slide -> Integer.valueOf(1).equals(slide.getSequenceNum()))
                .map(ProjectSlide::getImageUrl)
                .findFirst()
                .orElse(this.logoUrl);
    }

    // 소프트 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    private static void validateProjectPeriod(LocalDate startMonth, LocalDate endMonth) {
        if (startMonth == null || endMonth == null) {
            throw new CustomException(ProjectErrorCode.PROJECT_PERIOD_REQUIRED);
        }

        if (endMonth.isBefore(startMonth)) {
            throw new CustomException(ProjectErrorCode.INVALID_PROJECT_PERIOD);
        }
    }
}