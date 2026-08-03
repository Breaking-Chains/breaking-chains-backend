package com.breakingchains.controller;

import com.breakingchains.dto.*;
import com.breakingchains.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Auth & Sessions", description = "Authentication and session management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register User", description = "Creates a new local user account.")
    public ResponseEntity<ApiResponse<AuthDataDto>> register(@Valid @RequestBody RegisterRequest request) {
        AuthDataDto result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates local user credentials and issues tokens.")
    public ResponseEntity<ApiResponse<AuthDataDto>> login(@Valid @RequestBody LoginRequest request) {
        AuthDataDto result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/google")
    @Operation(summary = "Google OAuth Sign-In", description = "Authenticates or registers a user via Google OAuth ID Token.")
    public ResponseEntity<ApiResponse<AuthDataDto>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        AuthDataDto result = authService.googleAuth(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token", description = "Rotates a Refresh Token and generates a new JWT Access Token.")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout User", description = "Revokes the provided Refresh Token.")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        String token = (request != null) ? request.getRefreshToken() : null;
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
