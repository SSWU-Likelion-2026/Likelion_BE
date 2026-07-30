package com.likelion.likelion_BE.domain.recruit.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.recruit.dto.request.AdminRecruitmentRequest;
import com.likelion.likelion_BE.domain.recruit.dto.response.AdminRecruitmentResponse;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    // 모집 공고 생성
    @Transactional
    public AdminRecruitmentResponse createRecruitment(AdminRecruitmentRequest request) {

        // 1.  날짜 순서 검증 + 기수 중복 검증
        if (!request.isValidScheduleSequence()) {
            throw new CustomException(RecruitmentErrorCode.INVALID_SCHEDULE_SEQUENCE); // RECRUITMENT-4002
        }

        if (recruitmentRepository.existsByTerm(request.term())) {
            throw new CustomException(RecruitmentErrorCode.DUPLICATE_RECRUITMENT_TERM);
        }

        // 2. DTO -> RecruitmentPart 정적 팩토리 메서드로 변환
        List<RecruitmentPart> parts = (request.parts() != null) ?
                request.parts().stream()
                        .map(p -> RecruitmentPart.createPart(p.name(), p.description()))
                        .toList() : List.of();

        // 3. 정적 팩토리 메서드로 Entity 생성
        Recruitment recruitment = Recruitment.createRecruitment(
                request.term(),
                request.title(),
                request.status(),
                request.docStartAt(),
                request.docEndAt(),
                request.docResultAt(),
                request.interviewStartAt(),
                request.interviewEndAt(),
                request.finalResultAt(),
                parts
        );

        // 4. DB에 저장
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);

        // 5. Response DTO 정적 팩토리 메서드로 변환 후 반환
        return AdminRecruitmentResponse.from(savedRecruitment);
    }

    // 모집 공고 수정
    @Transactional
    public AdminRecruitmentResponse updateRecruitment(Long recruitmentId, AdminRecruitmentRequest request) {

        // 1. 날짜 순서 검증
        if (!request.isValidScheduleSequence()) {
            throw new CustomException(RecruitmentErrorCode.INVALID_SCHEDULE_SEQUENCE); // RECRUITMENT-4002
        }

        // 2. 수정할 모집 공고 존재 여부 확인
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));

        // 3. 기수 변경시 이미 존재하는 기수인지 확인
        if (!recruitment.getTerm().equals(request.term()) && recruitmentRepository.existsByTerm(request.term())) {
            throw new CustomException(RecruitmentErrorCode.DUPLICATE_RECRUITMENT_TERM);
        }

        // 파트 목록 정적 팩토리 메서드로 변환
        List<RecruitmentPart> newParts = (request.parts() != null) ?
                request.parts().stream()
                        .map(p -> RecruitmentPart.createPart(p.name(), p.description()))
                        .toList() : List.of();

        // 4. Entity 수정 - 더티 체킹
        recruitment.updateRecruitment(
                request.term(),
                request.title(),
                request.status(),
                request.docStartAt(),
                request.docEndAt(),
                request.docResultAt(),
                request.interviewStartAt(),
                request.interviewEndAt(),
                request.finalResultAt(),
                newParts
        );

        return AdminRecruitmentResponse.from(recruitment);
    }
}
