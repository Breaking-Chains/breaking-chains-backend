package com.breakingchains.service;

import com.breakingchains.dto.*;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.AuthProvider;
import com.breakingchains.model.RefreshToken;
import com.breakingchains.model.User;
import com.breakingchains.repository.RefreshTokenRepository;
import com.breakingchains.repository.UserRepository;
import com.breakingchains.security.GoogleAuthVerifier;
import com.breakingchains.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final GoogleAuthVerifier googleAuthVerifier;

    @Transactional
    public AuthDataDto register(RegisterRequest request) {
        log.info("Attempting registration for email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already in use: {}", request.getEmail());
            throw AppException.userExists("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already in use: {}", request.getUsername());
            throw AppException.userExists("Username already in use");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .username(request.getUsername().trim())
                .authProvider(AuthProvider.LOCAL)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        TokenResponse tokens = createAndSaveTokens(savedUser);

        return AuthDataDto.builder()
                .user(UserDto.fromEntity(savedUser))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public AuthDataDto login(LoginRequest request) {
        log.info("Authentication attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", request.getEmail());
                    return AppException.invalidCredentials("Invalid email or password");
                });

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - password mismatch for email: {}", request.getEmail());
            throw AppException.invalidCredentials("Invalid email or password");
        }

        log.info("User authenticated successfully with ID: {}", user.getId());
        TokenResponse tokens = createAndSaveTokens(user);

        return AuthDataDto.builder()
                .user(UserDto.fromEntity(user))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public AuthDataDto googleAuth(GoogleAuthRequest request) {
        log.info("Attempting Google OAuth authentication");
        GoogleAuthVerifier.GoogleUserPayload payload;
        try {
            payload = googleAuthVerifier.verify(request.getIdToken());
        } catch (Exception ex) {
            log.warn("Google OAuth verification failed: {}", ex.getMessage());
            throw AppException.unauthorized("Invalid or expired Google ID token");
        }

        if (payload == null) {
            log.warn("Google OAuth payload returned null");
            throw AppException.unauthorized("Invalid or expired Google ID token");
        }

        Optional<User> existingUser = userRepository.findByGoogleId(payload.getGoogleId());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            log.info("Google OAuth login for existing user ID: {}", user.getId());
        } else {
            Optional<User> userByEmail = userRepository.findByEmail(payload.getEmail());
            if (userByEmail.isPresent()) {
                user = userByEmail.get();
                user.setGoogleId(payload.getGoogleId());
                if (user.getAvatarUrl() == null) {
                    user.setAvatarUrl(payload.getPictureUrl());
                }
                log.info("Linked Google ID to existing email user ID: {}", user.getId());
            } else {
                String baseUsername = payload.getEmail().split("@")[0];
                String username = generateUniqueUsername(baseUsername);

                user = User.builder()
                        .email(payload.getEmail().toLowerCase().trim())
                        .fullName(payload.getName() != null ? payload.getName() : baseUsername)
                        .username(username)
                        .avatarUrl(payload.getPictureUrl())
                        .authProvider(AuthProvider.GOOGLE)
                        .googleId(payload.getGoogleId())
                        .build();
                log.info("Creating new user from Google OAuth: {}", payload.getEmail());
            }
            user = userRepository.save(user);
        }

        TokenResponse tokens = createAndSaveTokens(user);

        return AuthDataDto.builder()
                .user(UserDto.fromEntity(user))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        log.info("Attempting refresh token rotation");
        jwtProvider.validateRefreshToken(token);

        RefreshToken storedToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token rotation failed - token not found or revoked");
                    return AppException.invalidRefreshToken("Refresh token is invalid or revoked");
                });

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            log.warn("Refresh token rotation failed - token expired");
            throw AppException.invalidRefreshToken("Refresh token is expired");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);
        log.info("Refresh token rotated successfully for user ID: {}", user.getId());

        return createAndSaveTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
            log.info("Successfully revoked refresh token on logout");
        }
    }

    private TokenResponse createAndSaveTokens(User user) {
        String userIdStr = user.getId().toString();
        String accessToken = jwtProvider.generateAccessToken(userIdStr, user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(userIdStr);

        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                new Date(System.currentTimeMillis() + jwtProvider.getRefreshExpirationMs()).toInstant(),
                ZoneId.systemDefault()
        );

        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateUniqueUsername(String base) {
        String username = base.replaceAll("[^a-zA-Z0-9_]", "_");
        if (!userRepository.existsByUsername(username)) {
            return username;
        }
        int counter = 1;
        while (userRepository.existsByUsername(username + counter)) {
            counter++;
        }
        return username + counter;
    }
}
