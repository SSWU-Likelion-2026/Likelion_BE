package com.likelion.likelion_BE.domain.project.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.project.dto.request.ProjectCreateRequest;
import com.likelion.likelion_BE.domain.project.dto.response.ProjectCreateResponse;
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
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public ProjectCreateResponse createProject(String email, ProjectCreateRequest request) {
        // 1. 유저 조회 및 권한 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ProjectErrorCode.PROJECT_FORBIDDEN_CREATE)); // 유저가 없을 경우 처리

        validateAdminRole(user.getRole());

        // 2. 기술 스택 존재 유무 확인
        List<TechStack> techStacks = techStackRepository.findAllByIdIn(request.techStackIds());
        if (techStacks.size() != request.techStackIds().size()) {
            throw new CustomException(ProjectErrorCode.TECH_STACK_NOT_FOUND);
        }

        // 3. Slide 엔티티 변환 (순서 자동 매핑)
        AtomicInteger sequence = new AtomicInteger(1);
        List<ProjectSlide> slides = request.slideUrls().stream()
                .map(url -> ProjectSlide.createSlide(url, sequence.getAndIncrement()))
                .toList();

        // 4. Member 엔티티 변환
        List<ProjectMember> members = request.members().stream()
                .map(m -> ProjectMember.createMember(m.name(), m.part()))
                .toList();

        // 5. TechStack 매핑 엔티티 변환
        List<ProjectTechStack> projectTechStacks = techStacks.stream()
                .map(ProjectTechStack::createProjectTechStack)
                .toList();

        // 6. Project Aggregate 루트 생성 및 유저 ID 연결
        Project project = Project.createProject(
                user.getId(), // User 엔티티의 ID 적용
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
        return ProjectCreateResponse.from(savedProject);
    }

    private void validateAdminRole(Role role) {
        if (role != Role.LEADER && role != Role.MANAGER) {
            throw new CustomException(ProjectErrorCode.PROJECT_FORBIDDEN_CREATE);
        }
    }
}