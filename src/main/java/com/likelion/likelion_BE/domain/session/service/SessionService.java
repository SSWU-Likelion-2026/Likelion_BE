package com.likelion.likelion_BE.domain.session.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.project.enums.Part;
import com.likelion.likelion_BE.domain.session.dto.response.SessionDetailResponse;
import com.likelion.likelion_BE.domain.session.dto.response.SessionListResponse;
import com.likelion.likelion_BE.domain.session.entity.Session;
import com.likelion.likelion_BE.domain.session.exception.SessionErrorCode;
import com.likelion.likelion_BE.domain.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;

    // 세션 목록 조회
    public SessionListResponse getSessions(Integer term, Part part) {
        List<Session> sessions = sessionRepository.findByTermAndPartOrderByWeekNumberAsc(term, part);
        return SessionListResponse.of(term, part, sessions);
    }

    // 세션 상세 조회
    public SessionDetailResponse getSessionDetail(Long sessionId, Integer term, Part part, Integer weekNumber) {
        Session session = sessionRepository.findByTermAndPartAndWeekNumberWithLearningTopics(term, part, weekNumber)
                .orElseThrow(() -> new CustomException(SessionErrorCode.SESSION_NOT_FOUND));

        return SessionDetailResponse.from(session);
    }
}