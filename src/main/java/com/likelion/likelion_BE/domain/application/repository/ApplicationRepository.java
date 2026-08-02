package com.likelion.likelion_BE.domain.application.repository;

import com.likelion.likelion_BE.domain.application.entity.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 유저 Id와 모집 공고 Id로 기존 지원서 조회 (임시저장/제출 확인용)
    @EntityGraph(attributePaths = {"answers", "answers.question", "recruitmentPart"})
    Optional<Application> findByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

    // 유저 Id와 모집 공고 Id로 지원서 존재 여부 확인
    boolean existsByUserIdAndRecruitmentId(Long userId, Long recruitmentId);

}
