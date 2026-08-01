package com.breakingchains.controller;

import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.CompleteEmergencyRequest;
import com.breakingchains.dto.EmergencyContentResponse;
import com.breakingchains.dto.EmergencySessionResponse;
import com.breakingchains.dto.StartEmergencyRequest;
import com.breakingchains.model.User;
import com.breakingchains.service.EmergencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<EmergencyContentResponse>> startEmergencySession(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody StartEmergencyRequest request
    ) {
        EmergencyContentResponse response = emergencyService.startEmergencySession(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Emergency SOS session started", response));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ApiResponse<EmergencySessionResponse>> completeEmergencySession(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID sessionId,
            @RequestBody CompleteEmergencyRequest request
    ) {
        EmergencySessionResponse response = emergencyService.completeEmergencySession(currentUser, sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("Emergency session completed successfully", response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<EmergencySessionResponse>>> getEmergencyHistory(
            @AuthenticationPrincipal User currentUser
    ) {
        List<EmergencySessionResponse> history = emergencyService.getUserEmergencyHistory(currentUser);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
