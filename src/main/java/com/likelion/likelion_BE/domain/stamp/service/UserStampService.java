package com.likelion.likelion_BE.domain.stamp.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.common.s3.S3Uploader;
import com.likelion.likelion_BE.domain.stamp.dto.request.StampAuthRequest;
import com.likelion.likelion_BE.domain.stamp.dto.response.MissionListResponse;
import com.likelion.likelion_BE.domain.stamp.dto.response.MyStampResponse;
import com.likelion.likelion_BE.domain.stamp.dto.response.StampAuthResponse;
import com.likelion.likelion_BE.domain.stamp.entity.Mission;
import com.likelion.likelion_BE.domain.stamp.entity.UserStamp;
import com.likelion.likelion_BE.domain.stamp.exception.StampErrorCode;
import com.likelion.likelion_BE.domain.stamp.repository.MissionRepository;
import com.likelion.likelion_BE.domain.stamp.repository.UserStampRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserStampService {

    private final MissionRepository missionRepository;
    private final UserStampRepository userStampRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    private static final int DEFAULT_TERM = 14;

    // 1. 스탬프 미션 목록 조회
    public List<MissionListResponse> getMissions(Long userId, Integer term) {

        // term이 null이면 DB의 가장 최신 기수 조회
        int targetTerm = (term != null)
                ? term
                : missionRepository.findMaxTerm().orElse(DEFAULT_TERM);

        List<Mission> missions = missionRepository.findAllByTerm(targetTerm);

        // 현재 유저가 완료한 missionId 집합
        Set<Long> completedMissionIds = userStampRepository.findAllByUserId(userId).stream()
                .map(stamp -> stamp.getMission().getId())
                .collect(Collectors.toSet());

        return missions.stream()
                .map(mission -> MissionListResponse.of(
                        mission,
                        completedMissionIds.contains(mission.getId())
                ))
                .toList();
    }

    // 2. 스탬프 미션 인증
    @Transactional
    public StampAuthResponse authenticateMission(Long userId, Long missionId, MultipartFile image, StampAuthRequest request) {

        // 유저 및 미션 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(StampErrorCode.MISSION_NOT_FOUND));

        // [검증 1] 미션 기간 확인
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(mission.getStartAt()) || now.isAfter(mission.getEndAt())) {
            throw new CustomException(StampErrorCode.MISSION_NOT_IN_PROGRESS);
        }

        // [검증 2] 이미 도장을 찍었는지 중복 인증 확인
        if (userStampRepository.existsByUserIdAndMissionId(userId, missionId)) {
            throw new CustomException(StampErrorCode.STAMP_MISSION_ALREADY_STAMPED);
        }

        // S3 사진 업로드
        String authImageUrl = s3Uploader.upload(image, "stamps");


        // UserStamp 저장
        UserStamp userStamp = UserStamp.of(mission, user, authImageUrl, request.content());
        userStampRepository.save(userStamp);

        return StampAuthResponse.from(userStamp);
    }

    // 3. 마이 스탬프 조회
    public MyStampResponse getMyStamps(Long userId) {

        // 유저 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        // 유저가 획득한 전체 스탬프 목록 조회
        List<UserStamp> userStamps = userStampRepository.findAllByUserId(userId);

        return MyStampResponse.of(user.getName(), userStamps);
    }
}