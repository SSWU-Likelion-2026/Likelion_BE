package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminQuestionRequest;
import com.likelion.likelion_BE.domain.recruit.dto.response.AdminQuestionResponse;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentPartRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentQuestionRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminQuestionService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentQuestionRepository recruitmentQuestionRepository;
    private final RecruitmentPartRepository recruitmentPartRepository;

    // 모집 질문 등록
    @Transactional
    public List<AdminQuestionResponse> createQuestions (Long recruitmentId, List<AdminQuestionRequest> requests) {

        // 모집 공고 확인
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));

        // 요청 목록 내에서 중복(동일 요청 안에 같은 파트 + 문항 번호가 존재하는지) 1차 검증
        validateDuplicateInRequests(requests);

        // 엔티티 변환 및 검증
        List<RecruitmentQuestion> questions = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            AdminQuestionRequest request = requests.get(i);
            String prefix = (i + 1) + "번째 질문: ";

            // 파트 검증 및 조회
            RecruitmentPart recruitmentPart = validateAndGetRecruitmentPart(recruitmentId, request.partId(), prefix);
            // DB 기존 데이터와의 문항 순서 중복 검증
            validateQuestionNumberDuplicate(recruitmentId, request.partId(), request.questionNumber(), prefix);

            RecruitmentQuestion question = RecruitmentQuestion.createQuestion(
                    recruitment,
                    recruitmentPart,
                    request.questionNumber(),
                    request.content(),
                    request.maxLength(),
                    request.questionType(),
                    request.isRequired()
            );

            questions.add(question);
        }

        // 일괄 저장
        List<RecruitmentQuestion> savedQuestions = recruitmentQuestionRepository.saveAll(questions);

        return savedQuestions.stream()
                .map(AdminQuestionResponse::from)
                .toList();
    }

    // 모집 질문 수정
    @Transactional
    public AdminQuestionResponse updateQuestion(Long questionId, AdminQuestionRequest request) {

        // 모집 질문 확인
        RecruitmentQuestion question = recruitmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.QUESTION_NOT_FOUND));

        Long recruitmentId = question.getRecruitment().getId();
        String prefix = "";

        // 파트 검증 및 조회
        RecruitmentPart recruitmentPart = validateAndGetRecruitmentPart(recruitmentId, request.partId(), prefix);

        // 수정 시 문항 번호나 파트가 변경되었다면 중복 검증
        if (!isSameQuestionPosition(question, request.partId(), request.questionNumber())) {
            validateQuestionNumberDuplicate(recruitmentId, request.partId(), request.questionNumber(), prefix);
        }

        question.updateQuestion(
                recruitmentPart,
                request.questionNumber(),
                request.content(),
                request.maxLength(),
                request.questionType(),
                request.isRequired()
        );

        return AdminQuestionResponse.from(question);
    }

    // 모집 질문 삭제
    @Transactional
    public void deleteQuestion(Long questionId) {
        RecruitmentQuestion question = recruitmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.QUESTION_NOT_FOUND));

        recruitmentQuestionRepository.delete(question);
    }


    /**
     * 헬퍼 메서드 모음
     */

    // 파트 검증 및 엔티티 조회
    private RecruitmentPart validateAndGetRecruitmentPart(Long recruitmentId, Long partId, String prefix) {

        // 공통 질문 판별
        if (partId == null) {
            return null;
        }

        // 파트 존재 여부 판별
        RecruitmentPart recruitmentPart = recruitmentPartRepository.findById(partId)
                .orElseThrow(() -> new CustomException(
                        RecruitmentErrorCode.RECRUITMENT_PART_NOT_FOUND,
                        prefix + String.format("존재하지 않는 파트 Id(%d)입니다.", partId)
                ));

        // 해당 파트가 해당 모집 공고 소속인지 검증
        if (!recruitmentPart.getRecruitment().getId().equals(recruitmentId)) {
            throw new CustomException(
                    RecruitmentErrorCode.INVALID_RECRUITMENT_PART,
                    prefix + "해당 모집 공고에 속하지 않은 파트입니다."
            );
        }

        return recruitmentPart;
    }

    // 문항 순서 중복 검증
    private void validateQuestionNumberDuplicate(Long recruitmentId, Long partId, Long questionNumber, String prefix) {
        boolean isDuplicate = (partId != null)
                ? recruitmentQuestionRepository.existsByRecruitmentIdAndRecruitmentPartIdAndQuestionNumber(recruitmentId, partId, questionNumber)
                : recruitmentQuestionRepository.existsByRecruitmentIdAndRecruitmentPartIsNullAndQuestionNumber(recruitmentId, questionNumber);

        if (isDuplicate) {
            throw new CustomException(
                    RecruitmentErrorCode.DUPLICATE_QUESTION_NUMBER,
                    prefix + String.format("이미 DB에 존재하거나 중복된 문항 순서(%d번)입니다.", questionNumber)
            );
        }
    }

    // 문항 순서 중복 검증 (요청 리스트 내부 자체 검증, 여러건)
    private void validateDuplicateInRequests(List<AdminQuestionRequest> requests) {
        Set<String> seenKeys = new HashSet<>();

        for (int i = 0; i < requests.size(); i++) {
            AdminQuestionRequest req = requests.get(i);
            String key = req.partId() + "_" + req.questionNumber();

            if (!seenKeys.add(key)) {
                // 동일 요청 내에서 중복이 발견된 경우
                throw new CustomException(
                        RecruitmentErrorCode.DUPLICATE_QUESTION_NUMBER,
                        String.format("%d번째 질문: 요청 내에 중복된 파트 및 문항 순서(%d번)가 존재합니다.", i + 1, req.questionNumber())
                );
            }
        }
    }

    // 수정 시 자기 자신과의 위치(파트 및 순서)가 동일한지 확인
    private boolean isSameQuestionPosition(RecruitmentQuestion question, Long newPartId, Long newQuestionNumber) {
        Long currentPartId = question.getRecruitmentPart() != null ? question.getRecruitmentPart().getId() : null;
        return Objects.equals(currentPartId, newPartId) && question.getQuestionNumber().equals(newQuestionNumber);
    }

}