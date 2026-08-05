package com.likelion.likelion_BE.domain.user.service;

import com.likelion.likelion_BE.common.exception.CustomException;
import com.likelion.likelion_BE.config.jwt.JwtTokenProvider;
import com.likelion.likelion_BE.domain.user.dto.request.LoginRequest;
import com.likelion.likelion_BE.domain.user.dto.request.SignupRequest;
import com.likelion.likelion_BE.domain.user.dto.request.TokenRefreshRequest;
import com.likelion.likelion_BE.domain.user.dto.response.TokenRefreshResponse;
import com.likelion.likelion_BE.domain.user.dto.response.UserResponse;
import com.likelion.likelion_BE.domain.user.entity.EmailVerification;
import com.likelion.likelion_BE.domain.user.entity.RefreshToken;
import com.likelion.likelion_BE.domain.user.entity.User;
import com.likelion.likelion_BE.domain.user.exception.AuthErrorCode;
import com.likelion.likelion_BE.domain.user.repository.EmailVerificationRepository;
import com.likelion.likelion_BE.domain.user.repository.RefreshTokenRepository;
import com.likelion.likelion_BE.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final String SUNGSHIN_EMAIL_DOMAIN = "@sungshin.ac.kr";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * Registers a local user after validating the email domain, password confirmation,
     * email uniqueness, and email verification status.
     *
     * @param request the signup details
     * @return the registered user and issued authentication tokens
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase();

        if (!email.endsWith(SUNGSHIN_EMAIL_DOMAIN)) {
            throw new CustomException(AuthErrorCode.NOT_SUNGSHIN_EMAIL);
        }

        if (!request.password().equals(request.passwordCheck())) {
            throw new CustomException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        validateEmailVerified(email);

        User user = userRepository.save(
                User.createLocalUser(
                        email,
                        passwordEncoder.encode(request.password()),
                        request.name().trim(),
                        null,
                        null,
                        null
                )
        );

        TokenRefreshResponse tokens = issueTokens(user);

        return UserResponse.of(
                user,
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiresIn()
        );
    }

    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        TokenRefreshResponse tokens = issueTokens(user);

        return UserResponse.of(
                user,
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiresIn()
        );
    }

    @Transactional
    public TokenRefreshResponse reissue(TokenRefreshRequest request) {
        String refreshTokenValue = request.refreshToken();

        if (!jwtTokenProvider.validateToken(refreshTokenValue)
                || !jwtTokenProvider.isRefreshToken(refreshTokenValue)) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(jwtTokenProvider.getEmail(refreshTokenValue))
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        if (savedRefreshToken.isExpired()
                || !savedRefreshToken.getToken().equals(hashToken(refreshTokenValue))) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        return issueTokens(user);
    }

    /**
     * Logs out the user associated with the specified email address.
     *
     * @param email the user's email address
     * @throws CustomException if no user is associated with the email address
     */
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.UNAUTHORIZED));

        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Verifies that the specified email address has completed email verification.
     *
     * @param email the email address to verify
     * @throws CustomException if no verification record exists or the email address is not verified
     */
    private void validateEmailVerified(String email) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED));

        if (!verification.isVerified()) {
            throw new CustomException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    /**
     * Issues access and refresh tokens for a user while maintaining a single active refresh-token session.
     *
     * @return tokens and the access-token expiration time
     */
    private TokenRefreshResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        String refreshTokenValue = jwtTokenProvider.createRefreshToken(user.getEmail());

        /*
         * 한 사용자당 하나의 로그인 세션만 유지한다.
         * 새 로그인 또는 재발급 시 기존 Refresh Token은 폐기된다.
         */
        refreshTokenRepository.deleteByUser(user);

        refreshTokenRepository.save(
                RefreshToken.createRefreshToken(
                        hashToken(refreshTokenValue),
                        user,
                        LocalDateTime.now()
                                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000)
                )
        );

        return TokenRefreshResponse.of(
                accessToken,
                refreshTokenValue,
                jwtTokenProvider.getAccessTokenExpiration()
        );
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}

