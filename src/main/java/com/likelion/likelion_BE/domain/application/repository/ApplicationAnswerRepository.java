package com.likelion.likelion_BE.domain.application.repository;

import com.likelion.likelion_BE.domain.application.entity.ApplicationAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {

    // 특정 지원서의 전체 답변 목록 조회
    List<ApplicationAnswer> findByApplicationId(Long applicationId);

}
