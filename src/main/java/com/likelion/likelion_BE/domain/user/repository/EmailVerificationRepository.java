package com.likelion.likelion_BE.domain.user.repository;

import com.likelion.likelion_BE.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmail(String email);
}
