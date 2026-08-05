package com.likelion.likelion_BE.domain.user.repository;

import com.likelion.likelion_BE.domain.user.entity.RefreshToken;
import com.likelion.likelion_BE.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);
}
