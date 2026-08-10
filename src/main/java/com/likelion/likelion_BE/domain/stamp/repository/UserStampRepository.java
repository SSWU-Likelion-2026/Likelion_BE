package com.likelion.likelion_BE.domain.stamp.repository;

import com.likelion.likelion_BE.domain.stamp.entity.UserStamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserStampRepository extends JpaRepository<UserStamp, Long> {

    boolean existsByMissionId(Long missionId);

    // 특정 유저의 특정 미션 스탬프 존재 여부 (중복 인증 방지)
    boolean existsByUserIdAndMissionId(Long userId, Long missionId);

    // 특정 유저가 모은 전체 스탬프 목록 조회
    List<UserStamp> findAllByUserId(Long userId);

}
