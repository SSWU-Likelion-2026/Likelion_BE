package com.likelion.likelion_BE.domain.stamp.repository;

import com.likelion.likelion_BE.domain.stamp.entity.UserStamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStampRepository extends JpaRepository<UserStamp, Long> {

    boolean existsByMissionId(Long missionId);
}
