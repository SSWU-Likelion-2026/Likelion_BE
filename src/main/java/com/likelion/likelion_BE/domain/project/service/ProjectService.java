package com.likelion.likelion_BE.domain.project.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.project.dto.request.ProjectCreateUpdateRequest;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectCreateUpdateResponse;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectDetailResponse;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectListResponse;
import com.likelion.likelion_BE.domain.project.dto.response.RecentProjectResponse;
import com.likelion.likelion_BE.domain.project.entity.Project;
import com.likelion.likelion_BE.domain.project.entity.ProjectMember;
import com.likelion.likelion_BE.domain.project.entity.ProjectSlide;
import com.likelion.likelion_BE.domain.project.entity.ProjectTechStack;
import com.likelion.likelion_BE.domain.project.entity.TechStack;
import com.likelion.likelion_BE.domain.project.exception.ProjectErrorCode;
import com.likelion.likelion_BE.domain.project.repository.ProjectRepository;
import com.likelion.likelion_BE.domain.project.repository.TechStackRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.enums.Role;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TechStackRepository techStackRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectCreateUpdateResponse createProject(String email, ProjectCreateUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        validateAdminRole(user.getRole(), ProjectErrorCode.PROJECT_FORBIDDEN_CREATE);

        List<TechStack> techStacks = techStackRepository.findAllByIdIn(request.techStackIds());
        if (techStacks.size() != request.techStackIds().size()) {
            throw new CustomException(ProjectErrorCode.TECH_STACK_NOT_FOUND);
        }

        AtomicInteger sequence = new AtomicInteger(1);
        List<ProjectSlide> slides = request.slideUrls().stream()
                .map(url -> ProjectSlide.createSlide(url, sequence.getAndIncrement()))
                .toList();

        List<ProjectMember> members = request.members().stream()
                .map(m -> ProjectMember.createMember(m.name(), m.part()))
                .toList();

        List<ProjectTechStack> projectTechStacks = techStacks.stream()
                .map(ProjectTechStack::createProjectTechStack)
                .toList();

        Project project = Project.createProject(
                user,
                request.term(),
                request.hackathon(),
                request.title(),
                request.summary(),
                request.description(),
                request.logoUrl(),
                request.startMonth().atDay(1),
                request.endMonth().atDay(1),
                slides,
                members,
                projectTechStacks
        );

        Project savedProject = projectRepository.save(project);
        return ProjectCreateUpdateResponse.from(savedProject);
    }

    @Transactional
    public ProjectCreateUpdateResponse updateProject(Long projectId, String email, ProjectCreateUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CustomException(ProjectErrorCode.PROJECT_NOT_FOUND));

        project.validateOwnerAndAdminRole(user, ProjectErrorCode.PROJECT_FORBIDDEN_UPDATE);

        List<TechStack> techStacks = techStackRepository.findAllByIdIn(request.techStackIds());
        if (techStacks.size() != request.techStackIds().size()) {
            throw new CustomException(ProjectErrorCode.TECH_STACK_NOT_FOUND);
        }

        AtomicInteger sequence = new AtomicInteger(1);
        List<ProjectSlide> newSlides = request.slideUrls().stream()
                .map(url -> ProjectSlide.createSlide(url, sequence.getAndIncrement()))
                .toList();

        List<ProjectMember> newMembers = request.members().stream()
                .map(m -> ProjectMember.createMember(m.name(), m.part()))
                .toList();

        List<ProjectTechStack> newProjectTechStacks = techStacks.stream()
                .map(ProjectTechStack::createProjectTechStack)
                .toList();

        project.updateProject(
                request.term(),
                request.hackathon(),
                request.title(),
                request.summary(),
                request.description(),
                request.logoUrl(),
                request.startMonth().atDay(1),
                request.endMonth().atDay(1),
                newSlides,
                newMembers,
                newProjectTechStacks
        );

        return ProjectCreateUpdateResponse.from(project);
    }

    @Transactional
    public void deleteProject(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CustomException(ProjectErrorCode.PROJECT_NOT_FOUND));

        project.validateOwnerAndAdminRole(user, ProjectErrorCode.PROJECT_FORBIDDEN_DELETE);

        project.delete();
    }

    public List<RecentProjectResponse> getRecentProjects(int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return projectRepository.findAllByDeletedAtIsNull(pageRequest).stream()
                .map(RecentProjectResponse::from)
                .toList();
    }

    private void validateAdminRole(Role role, ProjectErrorCode errorCode) {
        if (role != Role.LEADER && role != Role.MANAGER) {
            throw new CustomException(errorCode);
        }
    }

    // 프로젝트 상세 조회 (권한 검증 포함)
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(Long projectId, String email) {
        Project project = projectRepository.findDetailByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new CustomException(ProjectErrorCode.PROJECT_NOT_FOUND));

        // 로그인하지 않은 사용자는 email이 null일 수 있음
        User user = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;

        // 권한 판단
        boolean canManage = project.isManageableBy(user);

        return ProjectDetailResponse.of(project, canManage);
    }

    public Page<ProjectListResponse> getProjects(Integer term, Pageable pageable) {
        Page<Project> projects = projectRepository.findAllByTerm(term, pageable);
        return projects.map(ProjectListResponse::from);
    }
}