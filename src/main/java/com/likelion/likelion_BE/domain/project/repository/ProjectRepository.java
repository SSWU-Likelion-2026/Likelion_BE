package com.likelion.likelion_BE.domain.project.repository;

import com.likelion.likelion_BE.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndDeletedAtIsNull(Long id);

    @EntityGraph(attributePaths = {"slides"})
    Page<Project> findAllByDeletedAtIsNull(Pageable pageable);

    // 기수별 목록 페이징 조회 (term이 null이면 전체 조회)
    // 썸네일 조회 시 N+1 문제를 방지하기 위해 slides를 EntityGraph로 함께 조회
    @EntityGraph(attributePaths = {"slides"})
    @Query("SELECT p FROM Project p " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:term IS NULL OR p.term = :term)")
    Page<Project> findAllByTerm(
            @Param("term") Integer term,
            Pageable pageable
    );

    // 상세 조회용: 자식 컬렉션들을 함께 Fetch
    @EntityGraph(attributePaths = {"techStacks", "techStacks.techStack"})
    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findDetailByIdAndDeletedAtIsNull(@Param("id") Long id);
}
