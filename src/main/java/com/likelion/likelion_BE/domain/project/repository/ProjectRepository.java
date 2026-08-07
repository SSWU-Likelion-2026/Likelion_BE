package com.likelion.likelion_BE.domain.project.repository;

import com.likelion.likelion_BE.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByIdAndDeletedAtIsNull(Long id);

    Page<Project> findAllByDeletedAtIsNull(Pageable pageable);

}