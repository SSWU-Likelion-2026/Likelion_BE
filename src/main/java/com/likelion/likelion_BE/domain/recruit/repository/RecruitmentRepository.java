package com.likelion.likelion_BE.domain.recruit.repository;

import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    // 해당 기수의 모집 공고가 이미 존재하는지 확인
    boolean existsByTerm(Integer term);

    // 기수 기반으로 모집 공고 조회
    Optional<Recruitment> findByTerm(Integer term);

    // 현재 상태에 해당하는 모집 공고 조회
    Optional<Recruitment> findFirstByStatusOrderByCreatedAtDesc(RecruitmentStatus status);
}
