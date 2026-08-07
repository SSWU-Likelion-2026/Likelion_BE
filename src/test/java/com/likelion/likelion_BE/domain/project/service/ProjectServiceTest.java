package com.likelion.likelion_BE.domain.project.service;

import com.likelion.likelion_BE.domain.project.dto.response.RecentProjectResponse;
import com.likelion.likelion_BE.domain.project.entity.Project;
import com.likelion.likelion_BE.domain.project.enums.Hackathon;
import com.likelion.likelion_BE.domain.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Test
    void 최근_프로젝트를_요청한_개수만큼_최신순으로_조회한다() {
        Project project = Project.createProject(
                1L,
                14,
                Hackathon.CENTRALTHON,
                "테스트 프로젝트",
                "프로젝트 한 줄 설명",
                "프로젝트 상세 설명",
                "https://example.com/thumbnail.png",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                List.of(),
                List.of(),
                List.of()
        );
        when(projectRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(project)));
        ProjectService projectService = new ProjectService(projectRepository);

        List<RecentProjectResponse> responses = projectService.getRecentProjects(3);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("테스트 프로젝트");
        assertThat(responses.get(0).summary()).isEqualTo("프로젝트 한 줄 설명");
        assertThat(responses.get(0).thumbnailUrl()).isEqualTo("https://example.com/thumbnail.png");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(projectRepository).findAllByDeletedAtIsNull(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageSize()).isEqualTo(3);
        assertThat(pageable.getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }
}
