package com.likelion.likelion_BE.domain.application.repository;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndRecruitment_TermAndPassStatus(
            Long userId,
            Integer term,
            PassStatus passStatus
    );

    // 유저 Id와 모집 공고 Id로 기존 지원서 조회 (임시저장/제출 확인용)
    @EntityGraph(attributePaths = {"answers", "answers.question", "recruitmentPart"})
    Optional<Application> findByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    // 유저 Id와 모집 공고 Id로 지원서 존재 여부 확인
    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    /**
     * 관리자 - 전체 지원서 목록 조회 (파트별, 합불 상태별 동적 필터링 + 페이징)
     */
    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.recruitmentPart " +
            "WHERE a.recruitment.id = :recruitmentId " +
            "AND (:partId IS NULL OR a.recruitmentPart.id = :partId) " +
            "AND (:passStatus IS NULL OR a.passStatus = :passStatus) " +
            "AND (:passStatus IS NULL OR a.passStatus = :passStatus)")
    Page<Application> findAllAdminApplications(
            @Param("recruitmentId") Long recruitmentId,
            @Param("partId") Long partId,
            @Param("passStatus") PassStatus passStatus,
            Pageable pageable
    );

    // 관리자 지원서 상세 조회 (답변, 질문, 파트, 모집공고 한 번에 Fetch)
    @EntityGraph(attributePaths = {"answers", "answers.question", "recruitmentPart", "recruitment"})
    Optional<Application> findDetailById(Long id);

    Optional<Application> findByUserIdAndSubmitStatus(Long userId, SubmitStatus submitStatus);

    Optional<Application> findByIdAndUserId(Long id, Long userId);
}
