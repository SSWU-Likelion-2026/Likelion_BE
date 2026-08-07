package com.likelion.likelion_BE.domain.stamp.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.stamp.dto.request.MissionCreateRequest;
import com.likelion.likelion_BE.domain.stamp.dto.request.MissionUpdateRequest;
import com.likelion.likelion_BE.domain.stamp.dto.response.MissionResponse;
import com.likelion.likelion_BE.domain.stamp.entity.Mission;
import com.likelion.likelion_BE.domain.stamp.exception.StampErrorCode;
import com.likelion.likelion_BE.domain.stamp.repository.MissionRepository;
import com.likelion.likelion_BE.domain.stamp.repository.UserStampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMissionService {

    private final MissionRepository missionRepository;
    private final UserStampRepository userStampRepository;

    // 1. 미션 등록 (List 일괄 등록)
    @Transactional
    public List<MissionResponse> createMissions(MissionCreateRequest request) {

        // 날짜 검증
        for (var item : request.missions()) {
            validateMissionDates(item.startAt(), item.endAt());
        }

        List<Mission> missions = request.missions().stream()
                .map(item -> Mission.of(
                        item.title(),
                        item.description(),
                        item.term(),
                        item.imageUrl(),
                        item.stampUrl(),
                        item.startAt(),
                        item.endAt()
                ))
                .toList();

        List<Mission> savedMissions = missionRepository.saveAll(missions);

        return savedMissions.stream()
                .map(MissionResponse::from)
                .toList();
    }

    // 2. 미션 수정 (단건)
    @Transactional
    public MissionResponse updateMission(Long missionId, MissionUpdateRequest request) {

        // 날짜 검증
        validateMissionDates(request.startAt(), request.endAt());

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(StampErrorCode.MISSION_NOT_FOUND));

        mission.update(
                request.title(),
                request.description(),
                request.term(),
                request.imageUrl(),
                request.stampUrl(),
                request.startAt(),
                request.endAt()
        );

        return MissionResponse.from(mission);
    }

    // 3. 미션 삭제
    @Transactional
    public void deleteMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(StampErrorCode.MISSION_NOT_FOUND));

        // 2. 이미 유저가 스탬프를 획득한 미션인지 검증 (있으면 MISSION-4002)
        if (userStampRepository.existsByMissionId(missionId)) {
            throw new CustomException(StampErrorCode.STAMP_MISSION_ALREADY_STAMPED);
        }

        missionRepository.delete(mission);
    }

    // 날짜 기간 검증 공통 메서드
    private void validateMissionDates(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt.isBefore(startAt) || endAt.isEqual(startAt)) {
            throw new CustomException(StampErrorCode.MISSION_INVALID_DATE_RANGE);
        }
    }
}
