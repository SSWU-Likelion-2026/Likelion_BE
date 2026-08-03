package com.likelion.likelion_BE.domain.faq.repository;

import com.likelion.likelion_BE.domain.faq.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    /**
     * 1. 특정 모집 기수(recruitmentId)의 전체 FAQ 목록 조회
     * - 프론트엔드가 한 번에 받아서 탭별(기획, 프론트, 백엔드)로 필터링할 때 사용
     */
    @Query("SELECT f FROM Faq f " +
            "LEFT JOIN FETCH f.recruitmentPart " +
            "WHERE f.recruitment.id = :recruitmentId " +
            "ORDER BY f.createdAt ASC")
    List<Faq> findAllByRecruitmentId(@Param("recruitmentId") Long recruitmentId);

    /**
     * 2. 특정 파트 FAQ 조회 (공통 FAQ + 특정 파트 FAQ만 쿼리로 필터링할 경우)
     * - partId가 null이면 공통 FAQ만 조회
     * - partId가 들어오면 공통 FAQ(part_id is null) + 지정한 파트 FAQ를 함께 조회
     */
    @Query("SELECT f FROM Faq f " +
            "LEFT JOIN FETCH f.recruitmentPart " +
            "WHERE f.recruitment.id = :recruitmentId " +
            "AND (f.recruitmentPart.id IS NULL OR f.recruitmentPart.id = :partId) " +
            "ORDER BY f.createdAt ASC")
    List<Faq> findByRecruitmentIdAndPartId(
            @Param("recruitmentId") Long recruitmentId,
            @Param("partId") Long partId
    );
}
