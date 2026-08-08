package com.likelion.likelion_BE.domain.session.repository;

import com.likelion.likelion_BE.domain.project.enums.Part;
import com.likelion.likelion_BE.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    // 1. 기수 및 파트별 세션 목록 조회 (주차 오름차순 정렬)
    List<Session> findByTermAndPartOrderByWeekNumberAsc(Integer term, Part part);

    // 2. 세션 상세 조회 (LearningTopics Fetch Join)
    @Query("SELECT DISTINCT s FROM Session s LEFT JOIN FETCH s.learningTopics lt WHERE s.id = :sessionId ORDER BY lt.sequenceNum ASC")
    Optional<Session> findByIdWithLearningTopics(@Param("sessionId") Long sessionId);
}