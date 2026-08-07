package com.likelion.likelion_BE.domain.project.service;

import com.likelion.likelion_BE.domain.project.dto.response.RecentProjectResponse;
import com.likelion.likelion_BE.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

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
}
