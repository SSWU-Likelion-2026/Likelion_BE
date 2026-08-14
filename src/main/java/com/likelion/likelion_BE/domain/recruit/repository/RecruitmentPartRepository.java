package com.likelion.likelion_BE.domain.recruit.repository;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentPartRepository extends JpaRepository<RecruitmentPart, Long> {

    List<RecruitmentPart> findAllByRecruitmentId(Long recruitmentId);
}
