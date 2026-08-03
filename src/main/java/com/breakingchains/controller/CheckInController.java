package com.breakingchains.controller;

import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.CheckInResponse;
import com.breakingchains.dto.LogCheckInRequest;
import com.breakingchains.model.User;
import com.breakingchains.service.CheckInService;
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
@RequestMapping("/api/v1/chains/{id}/logs")
@RequiredArgsConstructor
@Tag(name = "Daily Check-Ins", description = "Endpoints for logging daily progress and check-in history")
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    @Operation(summary = "Submit Check-In Log", description = "Logs daily check-in status (CLEAN, URGE_RESISTED, PEEKED_EDGED, SLIP_UP). If status is SLIP_UP, automated post-slip guidance is generated.")
    public ResponseEntity<ApiResponse<CheckInResponse>> logCheckIn(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId,
            @Valid @RequestBody LogCheckInRequest request
    ) {
        CheckInResponse response = checkInService.logCheckIn(currentUser, chainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Check-in logged successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get Check-In History", description = "Retrieves full log history for a habit chain.")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getChainLogs(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId
    ) {
        List<CheckInResponse> logs = checkInService.getChainLogs(currentUser, chainId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @DeleteMapping("/{logId}")
    @Operation(summary = "Delete Check-In Log", description = "Deletes check-in log and recalibrates the chain's streak state.")
    public ResponseEntity<ApiResponse<Void>> deleteLog(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId,
            @PathVariable("logId") UUID logId
    ) {
        checkInService.deleteLog(currentUser, chainId, logId);
        return ResponseEntity.ok(ApiResponse.success("Check-in log deleted successfully", null));
    }
}
