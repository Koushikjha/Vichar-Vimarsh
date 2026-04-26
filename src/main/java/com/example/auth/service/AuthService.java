package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.entity.RefreshToken;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.util.JwtUtil;
import com.example.user.entity.User;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepo;

    // ── Step 1: Send OTP ─────────────────────────────

    @Transactional
    public void sendOtp(SendOtpRequest request) {
        otpService.generateAndSend(request.getPhone());
    }

    // ── Step 2: Verify OTP → issue tokens ────────────

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {

        otpService.verify(request.getPhone(), request.getOtp());

        RegisterRequest registrationData = buildRegistrationData(request);

        User user = userService.findOrCreate(
                request.getPhone(),
                registrationData
        );

        if (!user.isAccountNonLocked()) {
            throw new IllegalStateException("Account is permanently suspended");
        }

        return buildTokenPair(user.getPhone(), user.getRole().name());
    }

    // ── Refresh token flow ───────────────────────────

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshToken stored = refreshTokenRepo
                .findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid or expired refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.setRevoked(true);
            refreshTokenRepo.save(stored);
            throw new IllegalStateException("Refresh token expired");
        }

        // 🔥 CHANGED: stored.getPhone() instead of username
        User user = userService.findByPhone(stored.getPhone());

        return buildTokenPair(user.getPhone(), user.getRole().name());
    }

    // ── Logout ───────────────────────────────────────

    @Transactional
    public void logout(String phone) {
        refreshTokenRepo.revokeAllByPhone(phone);
        log.info("All refresh tokens revoked for phone={}", phone);
    }

    // ── Token builder ────────────────────────────────

    private AuthResponse buildTokenPair(String phone, String role) {

        String accessToken = jwtUtil.generateAccessToken(
                phone,
                Map.of("role", role)
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .phone(phone)   // 🔥 renamed conceptually
                .expiresAt(Instant.now().plusSeconds(7L * 24 * 3600))
                .revoked(false)
                .build();

        refreshTokenRepo.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(86400)
                .build();
    }

    private RegisterRequest buildRegistrationData(VerifyOtpRequest req) {
        if (req.getFullName() == null && req.getUsername() == null) {
            return null;
        }

        RegisterRequest r = new RegisterRequest();
        r.setPhone(req.getPhone());
        r.setFullName(req.getFullName());
        r.setUsername(req.getUsername());
        return r;
    }
}