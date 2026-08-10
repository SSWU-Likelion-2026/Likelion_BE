package com.likelion.likelion_BE.domain.recruit.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.likelion_BE.domain.faq.entity.Faq;
import com.likelion.likelion_BE.domain.recruit.entity.Recruitment;
import com.likelion.likelion_BE.domain.recruit.entity.RecruitmentPart;
import com.likelion.likelion_BE.domain.recruit.enums.RecruitmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public record LandingPageResponse(
        RecruitmentInfo recruitment,
        List<PartInfo> parts,
        List<FaqInfo> faqs
) {
    public static LandingPageResponse of(Recruitment recruitment, List<RecruitmentPart> parts, List<Faq> faqs) {
        RecruitmentInfo recruitmentInfo = RecruitmentInfo.from(recruitment);
        List<PartInfo> partInfos = parts.stream().map(PartInfo::from).toList();
        List<FaqInfo> faqInfos = faqs.stream().map(FaqInfo::from).toList();

        return new LandingPageResponse(recruitmentInfo, partInfos, faqInfos);
    }

    public record RecruitmentInfo(
            Long recruitmentId,
            int term,
            String title,
            RecruitmentStatus status,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime docStartAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime docEndAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime docResultAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime interviewStartAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime interviewEndAt,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime finalResultAt
    ) {
        public static RecruitmentInfo from(Recruitment recruitment) {
            return new RecruitmentInfo(
                    recruitment.getId(),
                    recruitment.getTerm(),
                    recruitment.getTitle(),
                    recruitment.getStatus(),
                    recruitment.getDocStartAt(),
                    recruitment.getDocEndAt(),
                    recruitment.getDocResultAt(),
                    recruitment.getInterviewStartAt(),
                    recruitment.getInterviewEndAt(),
                    recruitment.getFinalResultAt()
            );
        }
    }

    public record PartInfo(
            Long partId,
            String name,
            String description
    ) {
        public static PartInfo from(RecruitmentPart part) {
            return new PartInfo(part.getId(), part.getName(), part.getDescription());
        }
    }

    public record FaqInfo(
            Long faqId,
            Long partId,
            String question,
            String answer
    ) {
        public static FaqInfo from(Faq faq) {
            Long partId = faq.getRecruitmentPart() != null ? faq.getRecruitmentPart().getId() : null;
            return new FaqInfo(faq.getId(), partId, faq.getQuestion(), faq.getAnswer());
        }
    }
}
