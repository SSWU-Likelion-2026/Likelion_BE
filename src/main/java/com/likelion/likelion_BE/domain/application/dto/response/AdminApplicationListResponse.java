package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record AdminApplicationListResponse(
        long totalElements,
        int totalPages,
        int currentPage,
        int size,
        List<ApplicationSummary> applications
) {
    public static AdminApplicationListResponse from(Page<Application> page) {
        List<ApplicationSummary> list = page.getContent().stream()
                .map(ApplicationSummary::from)
                .toList();

        return new AdminApplicationListResponse(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                list
        );
    }
    public record ApplicationSummary(
            Long applicationId,
            String applicantName,
            String studentId,
            String partName,
            SubmitStatus submitStatus,
            PassStatus passStatus,
            LocalDateTime submittedAt
    ) {
        public static ApplicationSummary from(Application application) {
            // TODO: 유저 엔티티/서비스 완성 시 실제 유저 정보(이름, 학번) 연결 필요
            String dummyName = "지원자" + application.getUserId();
            String dummyStudentId = "2024" + String.format("%04d", application.getUserId());

            return new ApplicationSummary(
                    application.getId(),
                    dummyName,
                    dummyStudentId,
                    application.getRecruitmentPart() != null ? application.getRecruitmentPart().getName() : null,
                    application.getSubmitStatus(),
                    application.getPassStatus(),
                    application.getUpdatedAt()
            );
        }
    }
}
