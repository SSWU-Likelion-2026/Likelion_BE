package com.likelion.likelion_BE.domain.recruit.repository;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentQuestionRepository extends JpaRepository<RecruitmentQuestion, Long> {
    // 공통 질문 순서 체크
    boolean existsByRecruitmentIdAndRecruitmentPartIsNullAndQuestionNumber(Long recruitmentId, Long questionNumber);

    // 파트별 질문 순서 체크
    boolean existsByRecruitmentIdAndRecruitmentPartIdAndQuestionNumber(Long recruitmentId, Long partId, Long questionNumber);

    List<RecruitmentQuestion> findAllByRecruitmentIdOrderByQuestionNumberAsc(Long recruitmentId);
}
