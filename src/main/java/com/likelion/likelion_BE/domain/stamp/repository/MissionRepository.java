package com.likelion.likelion_BE.domain.stamp.repository;

import com.likelion.likelion_BE.domain.stamp.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByTerm(Integer term);

    // 가장 큰(최신) 기수 번호 조회
    @Query("SELECT MAX(m.term) FROM Mission m")
    Optional<Integer> findMaxTerm();
}
