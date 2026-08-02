package com.likelion.likelion_BE.domain.application.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.dto.request.ApplicationSaveRequest;
import com.likelion.likelion_BE.domain.application.dto.response.CurrentQuestionsResponse;
import com.likelion.likelion_BE.domain.application.dto.response.MyApplicationResponse;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.entity.ApplicationAnswer;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.application.exception.ApplicationErrorCode;
import com.likelion.likelion_BE.domain.application.repository.ApplicationAnswerRepository;
import com.likelion.likelion_BE.domain.application.repository.ApplicationRepository;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentQuestion;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentPartRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentQuestionRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentPartRepository recruitmentPartRepository;
    private final RecruitmentQuestionRepository recruitmentQuestionRepository;

    // 현재 진행 중인 모집 공고 질문 목록 조회
    public CurrentQuestionsResponse getCurrentQuestions() {
        // 현재 OPEN 상태인 공고 조회
        Recruitment recruitment = getCurrentRecruitment();

        // 해당 모집의 전체 질문 조회
        List<RecruitmentQuestion> questions = recruitmentQuestionRepository.findAllByRecruitmentIdOrderByQuestionNumberAsc(recruitment.getId());

        // 공통 질문 그룹핑
        List<CurrentQuestionsResponse.QuestionInfo> commonQuestions = questions.stream()
                .filter(q -> q.getRecruitmentPart() == null)
                .map(CurrentQuestionsResponse.QuestionInfo::from)
                .toList();

        // 파트별 질문 그룹핑
        Map<RecruitmentPart, List<RecruitmentQuestion>> partMap = questions.stream()
                .filter(q -> q.getRecruitmentPart() != null)
                .collect(Collectors.groupingBy(RecruitmentQuestion::getRecruitmentPart));

        List<CurrentQuestionsResponse.PartQuestionGroup> partQuestions = partMap.entrySet().stream()
                .map(entry -> new CurrentQuestionsResponse.PartQuestionGroup(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getValue().stream()
                                .map(CurrentQuestionsResponse.QuestionInfo::from)
                                .toList()
                ))
                .toList();

        return new CurrentQuestionsResponse(
                recruitment.getId(),
                recruitment.getTerm(),
                commonQuestions,
                partQuestions
        );

    }

    // 지원서 임시저장 (검증 완화)
    @Transactional
    public Long saveDraft(Long userId, ApplicationSaveRequest request) {
        Recruitment recruitment = getCurrentRecruitment();

        // 모집 마감 여부 확인
        if (LocalDateTime.now().isAfter(recruitment.getDocEndAt())) {
            throw new CustomException(RecruitmentErrorCode.RECRUITMENT_CLOSED);
        }

        // 파트 Id 선택
        if (request.partId() == null) {
            throw new CustomException(ApplicationErrorCode.PART_REQUIRED);
        }

        RecruitmentPart part = getPart(recruitment, request.partId());

        // 기존 지원서가 있으면 가져오고, 없으면 신규 생성
        Application application = applicationRepository.findByUserIdAndRecruitmentId(userId, recruitment.getId())
                .orElseGet(() -> applicationRepository.save(
                        Application.createApplication(recruitment, part, userId)
                ));

        // 이미 최종 제출된 지원서는 수정 불가
        if (application.getSubmitStatus() == SubmitStatus.SUBMITTED) {
            throw new CustomException(ApplicationErrorCode.ALREADY_SUBMITTED);
        }

        // 파트 정보 및 임시저장 시각 업데이트
        application.updateDraft(part);

        // 답변 목록 저장/수정
        saveOrUpdateAnswers(application, request.answers());

        return application.getId();
    }

    // 지원서 최종 제출 (엄격한 검증)
    @Transactional
    public Long submitApplication(Long userId, ApplicationSaveRequest request) {
        Recruitment recruitment = getCurrentRecruitment();

        // [검증 1] 모집 기간 마감 여부 확인
        if (LocalDateTime.now().isAfter(recruitment.getDocEndAt())) {
            throw new CustomException(RecruitmentErrorCode.RECRUITMENT_CLOSED);
        }

        // [검증 2] 지원 파트 선택 필수
        if (request.partId() == null) {
            throw new CustomException(ApplicationErrorCode.PART_REQUIRED);
        }

        RecruitmentPart part = getPart(recruitment, request.partId());

        // 기존 지원서 조회 또는 새로 생성
        Application application = applicationRepository.findByUserIdAndRecruitmentId(userId, recruitment.getId())
                .orElseGet(() -> applicationRepository.save(
                        Application.createApplication(recruitment, part, userId)
                ));

        // [검증 3] 이미 제출 완료된 지원서인지 확인
        if (application.getSubmitStatus() == SubmitStatus.SUBMITTED) {
            throw new CustomException(ApplicationErrorCode.ALREADY_SUBMITTED);
        }

        saveOrUpdateAnswers(application, request.answers());

        // [검증 4] 필수 질문 답변 여부 및 글자 수 제한 검증
        validateAnswersForSubmit(recruitment.getId(), part.getId(), application.getAnswers());

        // 제출 상태(SUBMITTED) 변경
        application.submit(part);

        return application.getId();
    }

    /**
     * 헬퍼 메서드 모음
     */

    // 현재 OPEN 상태인 공고 조회
    private Recruitment getCurrentRecruitment() {
        return recruitmentRepository.findFirstByStatusOrderByCreatedAtDesc(RecruitmentStatus.OPEN)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));
    }

    // 모집 파트 Id로 파트 조회
    private RecruitmentPart getPart(Recruitment recruitment, Long partId) {
        RecruitmentPart part = recruitmentPartRepository.findById(partId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_PART_NOT_FOUND));

        // 파트가 현재 모집 공고의 파트가 아니면 예외 발생
        if (!part.getRecruitment().getId().equals(recruitment.getId())) {
            throw new CustomException(RecruitmentErrorCode.INVALID_RECRUITMENT_PART); // 에러코드 추가 필요시
        }

        return part;
    }

    // 답변 저장/수정 로직
    private void saveOrUpdateAnswers(Application application, List<ApplicationSaveRequest.AnswerInput> inputs) {
        if (inputs == null || inputs.isEmpty()) return;

        // 중복된 questionId가 있는지 확인
        Set<Long> questionIds = inputs.stream()
                .map(ApplicationSaveRequest.AnswerInput::questionId)
                .collect(Collectors.toSet());

        Map<Long, RecruitmentQuestion> questionMap = recruitmentQuestionRepository.findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(RecruitmentQuestion::getId, Function.identity()));

        // 존재하지 않는 questionId가 들어왔을 경우 예외 처리
        if (questionMap.size() != questionIds.size()) {
            throw new CustomException(RecruitmentErrorCode.QUESTION_NOT_FOUND);
        }

        // 기존에 등록되어 있던 답변 목록을 Map으로 구성
        Map<Long, ApplicationAnswer> existingAnswerMap = application.getAnswers().stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity()));

        for (ApplicationSaveRequest.AnswerInput input : inputs) {
            RecruitmentQuestion question = questionMap.get(input.questionId());
            String trimmedContent = input.content() != null ? input.content().trim() : "";

            if (existingAnswerMap.containsKey(input.questionId())) {
                // 기존 답변 업데이트
                existingAnswerMap.get(input.questionId()).updateContent(trimmedContent);
            } else {
                // 신규 답변 생성 및 연관관계 추가
                ApplicationAnswer.createAnswer(application, question, trimmedContent);
            }
        }
    }

    // 제출 시 엄격한 답변 검증 로직
    private void validateAnswersForSubmit(Long recruitmentId, Long partId, List<ApplicationAnswer> inputs) {
        // 해당 모집의 (공통 질문 + 선택 파트 질문) 전체 가져오기
        List<RecruitmentQuestion> requiredQuestions = recruitmentQuestionRepository
                .findAllByRecruitmentIdOrderByQuestionNumberAsc(recruitmentId).stream()
                .filter(q -> q.getRecruitmentPart() == null || q.getRecruitmentPart().getId().equals(partId))
                .toList();

        // 허용된 질문 Id 목록 추출
        Set<Long> validQuestionIds = requiredQuestions.stream()
                .map(RecruitmentQuestion::getId)
                .collect(Collectors.toSet());

        // 유저가 제출한 질문 Id 중 허용되지 않은(잘못된/타 파트) 질문이 있는지 검증
        Map<Long, String> answerMap = (inputs != null) ? inputs.stream()
                .filter(a -> validQuestionIds.contains(a.getQuestion().getId())) // 현재 파트/공통 질문에 속한 답변만
                .collect(Collectors.toMap(
                        a -> a.getQuestion().getId(),
                        a -> a.getContent() == null ? "" : a.getContent(),
                        (existing, replacement) -> replacement
                )) : Collections.emptyMap();

        for (RecruitmentQuestion question : requiredQuestions) {
            String userContent = answerMap.getOrDefault(question.getId(), "").trim();

            // 1) 필수 질문인데 안 적었거나 공백인 경우
            if (Boolean.TRUE.equals(question.getIsRequired()) && userContent.isEmpty()) {
                throw new CustomException(ApplicationErrorCode.REQUIRED_QUESTION_MISSING);
            }

            // 2) 최대 글자 수를 초과한 경우
            if (question.getMaxLength() != null && userContent.length() > question.getMaxLength()) {
                throw new CustomException(ApplicationErrorCode.ANSWER_LENGTH_EXCEEDED);
            }
        }
    }

    public MyApplicationResponse getMyApplication(Long userId) {
        Recruitment recruitment = getCurrentRecruitment();

        // 💡 해당 유저가 현재 기수에 작성한 지원서 조회
        Application application = applicationRepository.findByUserIdAndRecruitmentId(userId, recruitment.getId())
                .orElseThrow(() -> new CustomException(ApplicationErrorCode.APPLICATION_NOT_FOUND));

        return MyApplicationResponse.from(application);
    }
}
