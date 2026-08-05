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

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Builder.Default
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_sent_at", nullable = false)
    private LocalDateTime lastSentAt;

    /**
     * Creates a new email verification record with the supplied email, encrypted code, and expiration time.
     *
     * @param email         the email address to verify
     * @param encryptedCode the encrypted verification code
     * @param expiresAt     the time at which the verification expires
     * @return a new unverified email verification record
     */
    public static EmailVerification createEmailVerification(
            String email,
            String encryptedCode,
            LocalDateTime expiresAt
    ) {
        LocalDateTime now = LocalDateTime.now();

        return EmailVerification.builder()
                .email(email)
                .code(encryptedCode)
                .expiresAt(expiresAt)
                .createdAt(now)
                .lastSentAt(now)
                .build();
    }

    // 인증 완료 처리
    public void verify() {
        this.verified = true;
    }

    /**
     * Determines whether the verification has expired.
     *
     * @return {@code true} if the current time is after the expiration time, {@code false} otherwise.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Reissues the verification code and resets the verification status.
     *
     * @param encryptedCode the replacement encrypted verification code
     * @param newExpiresAt the expiration time for the replacement code
     */
    public void reissueCode(String encryptedCode, LocalDateTime newExpiresAt) {
        this.code = encryptedCode;
        this.expiresAt = newExpiresAt;
        this.lastSentAt = LocalDateTime.now();
        this.verified = false;
    }

    /**
     * Determines whether another verification code can be sent.
     *
     * @param cooldownSeconds the required interval since the last code was sent
     * @return {@code true} if the cooldown has elapsed, {@code false} otherwise
     */
    public boolean canResendAfter(long cooldownSeconds) {
        return !this.lastSentAt
                .plusSeconds(cooldownSeconds)
                .isAfter(LocalDateTime.now());
    }
}
