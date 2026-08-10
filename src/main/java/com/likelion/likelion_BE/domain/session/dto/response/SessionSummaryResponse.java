package com.likelion.likelion_BE.domain.session.dto.response;

import com.likelion.likelion_BE.domain.session.entity.Session;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionSummaryResponse {

    private Long sessionId;
    private Integer weekNumber;
    private String title;
    private String subTitle;

    public static SessionSummaryResponse from(Session session) {
        return SessionSummaryResponse.builder()
                .sessionId(session.getId())
                .weekNumber(session.getWeekNumber())
                .title(session.getTitle())
                .subTitle(session.getSubTitle())
                .build();
    }
}