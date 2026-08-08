package com.likelion.likelion_BE.domain.session.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.domain.session.dto.request.SessionCommentCreateUpdateRequest;
import com.likelion.likelion_BE.domain.session.dto.response.SessionCommentResponse;
import com.likelion.likelion_BE.domain.session.entity.Session;
import com.likelion.likelion_BE.domain.session.entity.SessionComment;
import com.likelion.likelion_BE.domain.session.exception.SessionErrorCode;
import com.likelion.likelion_BE.domain.session.repository.SessionCommentRepository;
import com.likelion.likelion_BE.domain.session.repository.SessionRepository;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionCommentService {

    private final SessionCommentRepository commentRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    // 1. 세션 후기 목록 조회
    public List<SessionCommentResponse> getComments(Long sessionId, String currentUserEmail) {
        // 세션 존재 여부 검증 추가
        if (!sessionRepository.existsById(sessionId)) {
            throw new CustomException(SessionErrorCode.SESSION_NOT_FOUND);
        }

        List<SessionComment> comments = commentRepository.findActiveCommentsBySessionId(sessionId);
        return comments.stream()
                .map(comment -> SessionCommentResponse.of(comment, currentUserEmail))
                .toList();
    }

    // 2. 세션 후기 등록
    @Transactional
    public SessionCommentResponse createComment(Long sessionId, String email, SessionCommentCreateUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(SessionErrorCode.SESSION_NOT_FOUND));

        SessionComment comment = SessionComment.createComment(session, user, request.content());
        SessionComment savedComment = commentRepository.save(comment);

        return SessionCommentResponse.of(savedComment, email);
    }

    // 3. 세션 후기 수정
    @Transactional
    public SessionCommentResponse updateComment(Long commentId, String email, SessionCommentCreateUpdateRequest request) {
        // 삭제되지 않은 댓글만 조회 (이미 삭제된 경우 알아서 NOT_FOUND 예외 발생)
        SessionComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new CustomException(SessionErrorCode.SESSION_COMMENT_NOT_FOUND));

        // 본인 검증
        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException(SessionErrorCode.SESSION_COMMENT_FORBIDDEN);
        }

        comment.updateContent(request.content());
        return SessionCommentResponse.of(comment, email);
    }

    // 4. 세션 후기 삭제 (Soft Delete)
    @Transactional
    public void deleteComment(Long commentId, String email) {
        // 삭제되지 않은 댓글만 조회
        SessionComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new CustomException(SessionErrorCode.SESSION_COMMENT_NOT_FOUND));

        // 본인 검증
        if (!comment.getUser().getEmail().equals(email)) {
            throw new CustomException(SessionErrorCode.SESSION_COMMENT_FORBIDDEN);
        }

        comment.softDelete();
    }
}