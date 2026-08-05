package com.likelion.likelion_BE.domain.user.repository;

import com.likelion.likelion_BE.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    /**
 * Finds an email verification record by email address.
 *
 * @param email the email address to search for
 * @return the matching email verification record, if one exists
 */
Optional<EmailVerification> findByEmail(String email);
}
