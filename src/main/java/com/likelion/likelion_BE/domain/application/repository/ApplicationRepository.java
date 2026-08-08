package com.likelion.likelion_BE.domain.application.repository;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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

    // SUBMITTED 조회용 - 여러 모집에 걸쳐 리스트로 반환
    List<Application> findByUserIdAndSubmitStatusOrderByIdDesc(Long userId, SubmitStatus submitStatus);

    // DRAFT 조회용 - 비즈니스 규칙상 최대 1개이므로 단일 반환
    // 방어적으로 가장 최근 것 하나만 선택 (만에 하나 규칙이 깨져 2개 이상 생기더라도 안전하게 동작)
    Optional<Application> findFirstByUserIdAndSubmitStatusOrderByIdDesc(Long userId, SubmitStatus submitStatus);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Application a WHERE a.id = :id")
    Optional<Application> findByIdForUpdate(@Param("id") Long id);

}
