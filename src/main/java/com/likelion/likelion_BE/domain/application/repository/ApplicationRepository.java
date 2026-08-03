package com.likelion.likelion_BE.domain.application.repository;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 유저 Id와 모집 공고 Id로 기존 지원서 조회 (임시저장/제출 확인용)
    @EntityGraph(attributePaths = {"answers", "answers.question", "recruitmentPart"})
    Optional<Application> findByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    // 유저 Id와 모집 공고 Id로 지원서 존재 여부 확인
    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    /**
     * 관리자 - 전체 지원서 목록 조회 (파트별, 합불 상태별 동적 필터링 + 페이징)
     */
    @Query("SELECT a FROM Application a " +
            "WHERE a.recruitment.id = :recruitmentId " +
            "AND (:partId IS NULL OR a.recruitmentPart.id = :partId) " +
            "AND (:passStatus IS NULL OR a.passStatus = :passStatus) " +
            "ORDER BY a.createdAt DESC")
    Page<Application> findAllAdminApplications(
            @Param("recruitmentId") Long recruitmentId,
            @Param("partId") Long partId,
            @Param("passStatus") PassStatus passStatus,
            Pageable pageable
    );
}
