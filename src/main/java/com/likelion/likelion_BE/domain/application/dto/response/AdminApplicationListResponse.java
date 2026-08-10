package com.likelion.likelion_BE.domain.application.dto.response;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.application.entity.Application;
import com.likelion.likelion_BE.domain.application.enums.PassStatus;
import com.likelion.likelion_BE.domain.application.enums.SubmitStatus;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AdminApplicationListResponse(
        long totalElements,
        int totalPages,
        int currentPage,
        int size,
        List<ApplicationSummary> applications
) {
    public static AdminApplicationListResponse from(Page<Application> page, Map<Long, User> userMap) {
        List<ApplicationSummary> list = page.getContent().stream()
                .map(application -> {
                    User user = userMap.get(application.getUserId());
                    return ApplicationSummary.from(application, user);
                })
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
        public static ApplicationSummary from(Application application, User user) {
            if (user == null) {
                throw new CustomException(AuthErrorCode.USER_NOT_FOUND);
            }

            return new ApplicationSummary(
                    application.getId(),
                    user.getName(),
                    user.getStudentId(),
                    application.getRecruitmentPart() != null ? application.getRecruitmentPart().getName() : null,
                    application.getSubmitStatus(),
                    application.getPassStatus(),
                    application.getUpdatedAt()
            );
        }
    }
}
