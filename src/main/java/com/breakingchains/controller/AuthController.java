package com.breakingchains.controller;

import com.breakingchains.dto.*;
import com.breakingchains.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDataDto>> register(@Valid @RequestBody RegisterRequest request) {
        AuthDataDto result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDataDto>> login(@Valid @RequestBody LoginRequest request) {
        AuthDataDto result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthDataDto>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        AuthDataDto result = authService.googleAuth(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        String token = (request != null) ? request.getRefreshToken() : null;
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
