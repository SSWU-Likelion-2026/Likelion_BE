package com.likelion.likelion_BE.domain.stamp.repository;

import com.likelion.likelion_BE.domain.stamp.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {

}
