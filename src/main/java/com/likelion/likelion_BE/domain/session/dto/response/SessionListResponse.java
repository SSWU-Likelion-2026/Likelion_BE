package com.likelion.likelion_BE.domain.session.dto.response;

import com.likelion.likelion_BE.domain.project.enums.Part;
import com.likelion.likelion_BE.domain.session.entity.Session;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SessionListResponse {

    private Integer term;
    private String part;
    private List<SessionSummaryResponse> sessions;

    public static SessionListResponse of(Integer term, Part part, List<Session> sessions) {
        return SessionListResponse.builder()
                .term(term)
                .part(part.name())
                .sessions(sessions.stream()
                        .map(SessionSummaryResponse::from)
                        .toList())
                .build();
    }
}