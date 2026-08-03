package com.likelion.likelion_BE.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_verification_id")
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Builder.Default
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성 메서드
    public static EmailVerification createEmailVerification(String email, String code, LocalDateTime expiresAt) {
        return EmailVerification.builder()
                .email(email)
                .code(code)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 인증 완료 처리
    public void verify() {
        this.verified = true;
    }

    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    // 코드 재발급
    public void reissueCode(String newCode, LocalDateTime newExpiresAt) {
        this.code = newCode;
        this.expiresAt = newExpiresAt;
        this.verified = false;
    }
}
