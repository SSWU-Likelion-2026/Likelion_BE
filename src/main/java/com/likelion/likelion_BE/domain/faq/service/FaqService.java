package com.likelion.likelion_BE.domain.faq.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.faq.dto.request.FaqRequest;
import com.likelion.likelion_BE.domain.faq.dto.request.FaqUpdateRequest;
import com.likelion.likelion_BE.domain.faq.dto.response.FaqResponse;
import com.likelion.likelion_BE.domain.faq.entity.Faq;
import com.likelion.likelion_BE.domain.faq.exception.FaqErrorCode;
import com.likelion.likelion_BE.domain.faq.repository.FaqRepository;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.exception.RecruitmentErrorCode;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentPartRepository;
import com.likelion.likelion_BE.domain.recruit.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentPartRepository recruitmentPartRepository;

    // FAQ 등록
    @Transactional
    public List<FaqResponse> createFaq(FaqRequest request) {
        Recruitment recruitment = getRecruitmentOrThrow(request.recruitmentId());

        List<Faq> faqs = request.faqs().stream()
                .map(item -> {
                    RecruitmentPart part = getValidPartOrNull(item.partId(), recruitment.getId());
                    return Faq.of(recruitment, part, item.question(), item.answer());
                })
                .toList();

        return faqRepository.saveAll(faqs).stream()
                .map(FaqResponse::from)
                .toList();
    }

    // FAQ 수정
    @Transactional
    public FaqResponse updateFaq(Long faqId, FaqUpdateRequest request) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(FaqErrorCode.FAQ_NOT_FOUND));

        // 기존 FAQ가 속한 recruitmentId 기준으로 파트 유효성 검증
        RecruitmentPart part = getValidPartOrNull(request.partId(), faq.getRecruitment().getId());

        faq.update(part, request.question(), request.answer());

        return FaqResponse.from(faq);
    }

    // FAQ 삭제
    @Transactional
    public void deleteFaq(Long faqId) {
        Faq faq = faqRepository.findById(faqId)
                .orElseThrow(() -> new CustomException(FaqErrorCode.FAQ_NOT_FOUND));

        faqRepository.delete(faq);
    }


    // 헬퍼 메서드: Recruitment 검증 및 조회
    private Recruitment getRecruitmentOrThrow(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_NOT_FOUND));
    }

    // 헬퍼 메서드: partId 검증 (null이면 공통 FAQ, 값이 있으면 유효한 파트인지 확인)
    private RecruitmentPart getValidPartOrNull(Long partId, Long recruitmentId) {
        if (partId == null) {
            return null;
        }

        RecruitmentPart part = recruitmentPartRepository.findById(partId)
                .orElseThrow(() -> new CustomException(RecruitmentErrorCode.RECRUITMENT_PART_NOT_FOUND));

        // 💡 파트가 해당 모집 공고(recruitmentId)에 속해있는지도 함께 검증!
        if (!part.getRecruitment().getId().equals(recruitmentId)) {
            throw new CustomException(RecruitmentErrorCode.INVALID_RECRUITMENT_PART);
        }

        return part;
    }
}
