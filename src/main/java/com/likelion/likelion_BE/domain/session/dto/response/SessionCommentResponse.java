package com.likelion.likelion_BE.domain.session.dto.response;

import com.likelion.likelion_BE.domain.session.entity.SessionComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SessionCommentResponse {

    private Long commentId;
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private String content;
    private Boolean isOwner; // 내가 작성한 댓글인지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SessionCommentResponse of(SessionComment comment, String currentUserEmail) {
        boolean isOwner = currentUserEmail != null && currentUserEmail.equals(comment.getUser().getEmail());

        return SessionCommentResponse.builder()
                .commentId(comment.getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .content(comment.getContent())
                .isOwner(isOwner)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}