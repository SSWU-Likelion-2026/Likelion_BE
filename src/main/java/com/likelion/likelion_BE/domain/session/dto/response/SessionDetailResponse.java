package com.likelion.likelion_BE.domain.session.dto.response;

import com.likelion.likelion_BE.domain.session.entity.Session;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SessionDetailResponse {

    private Long sessionId;
    private Integer term;
    private Integer weekNumber;
    private String part;
    private String title;
    private String subTitle;
    private String content;
    private String thumbnailUrl;
    private List<LearningTopicResponse> learningTopics;

    public static SessionDetailResponse from(Session session) {
        return SessionDetailResponse.builder()
                .sessionId(session.getId())
                .term(session.getTerm())
                .weekNumber(session.getWeekNumber())
                .part(session.getPart().name())
                .title(session.getTitle())
                .subTitle(session.getSubTitle())
                .content(session.getContent())
                .thumbnailUrl(session.getThumbnailUrl())
                .learningTopics(session.getLearningTopics().stream()
                        .map(LearningTopicResponse::from)
                        .toList())
                .build();
    }
}
