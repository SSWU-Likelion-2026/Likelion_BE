package com.likelion.likelion_BE.domain.recruit.repository;

import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentAlertRepository extends JpaRepository<RecruitmentAlert, Long> {

    // 1. 아직 메일 안 받은 대기열에 이 이메일이 이미 있는가?
    boolean existsByEmailAndIsSentFalse(String email);

    // 2. 나중에 메일 보낼 때: 아직 안 보낸 사람 전원 조회
    List<RecruitmentAlert> findByIsSentFalse();
}
