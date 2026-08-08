package com.likelion.likelion_BE.domain.session.repository;

import com.likelion.likelion_BE.domain.session.entity.SessionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionCommentRepository extends JpaRepository<SessionComment, Long> {

    // 삭제되지 않은(deletedAt IS NULL) 댓글 목록만 최신순/작성순으로 조회
    @Query("SELECT sc FROM SessionComment sc JOIN FETCH sc.user WHERE sc.session.id = :sessionId AND sc.deletedAt IS NULL ORDER BY sc.createdAt ASC")
    List<SessionComment> findActiveCommentsBySessionId(@Param("sessionId") Long sessionId);

    Optional<SessionComment> findByIdAndDeletedAtIsNull(Long id);
}