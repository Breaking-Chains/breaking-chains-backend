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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emergency")
@RequiredArgsConstructor
@Tag(name = "SOS & Urge Surfing", description = "Urgent urge-surfing SOS session modules")
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping("/start")
    @Operation(summary = "Start Emergency SOS Session", description = "Initiates an urge-surfing intervention session with immediate dhikr, breathing exercises, and reflection.")
    public ResponseEntity<ApiResponse<EmergencyContentResponse>> startEmergencySession(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody StartEmergencyRequest request
    ) {
        EmergencyContentResponse response = emergencyService.startEmergencySession(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Emergency SOS session started", response));
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Complete Emergency SOS Session", description = "Finalizes an active SOS session, recording whether the urge was successfully survived.")
    public ResponseEntity<ApiResponse<EmergencySessionResponse>> completeEmergencySession(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID sessionId,
            @RequestBody CompleteEmergencyRequest request
    ) {
        EmergencySessionResponse response = emergencyService.completeEmergencySession(currentUser, sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("Emergency session completed successfully", response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Emergency SOS History", description = "Retrieves historical logs of completed or aborted urge-surfing emergency sessions.")
    public ResponseEntity<ApiResponse<List<EmergencySessionResponse>>> getEmergencyHistory(
            @AuthenticationPrincipal User currentUser
    ) {
        List<EmergencySessionResponse> history = emergencyService.getUserEmergencyHistory(currentUser);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
