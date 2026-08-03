package com.breakingchains.controller;

import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.CreateHabitChainRequest;
import com.breakingchains.dto.HabitChainResponse;
import com.breakingchains.dto.UpdateHabitChainRequest;
import com.breakingchains.model.ChainStatus;
import com.breakingchains.model.User;
import com.breakingchains.service.HabitChainService;
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
@RequestMapping("/api/v1/chains")
@RequiredArgsConstructor
@Tag(name = "Chains & Streaks", description = "Endpoints for creating and tracking habit streak chains")
public class HabitChainController {

    private final HabitChainService habitChainService;

    @PostMapping
    @Operation(summary = "Create Habit Chain", description = "Initializes a new habit recovery or streak tracking chain.")
    public ResponseEntity<ApiResponse<HabitChainResponse>> createChain(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateHabitChainRequest request
    ) {
        HabitChainResponse response = habitChainService.createChain(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Habit chain created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get User Habit Chains", description = "Lists habit chains owned by the authenticated user.")
    public ResponseEntity<ApiResponse<List<HabitChainResponse>>> getUserChains(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) ChainStatus status
    ) {
        List<HabitChainResponse> chains = habitChainService.getUserChains(currentUser, status);
        return ResponseEntity.ok(ApiResponse.success(chains));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Habit Chain by ID", description = "Retrieves a single habit chain by its unique identifier.")
    public ResponseEntity<ApiResponse<HabitChainResponse>> getChainById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id
    ) {
        HabitChainResponse response = habitChainService.getChainById(currentUser, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Habit Chain", description = "Updates chain configuration, target days, privacy level, or status.")
    public ResponseEntity<ApiResponse<HabitChainResponse>> updateChain(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateHabitChainRequest request
    ) {
        HabitChainResponse response = habitChainService.updateChain(currentUser, id, request);
        return ResponseEntity.ok(ApiResponse.success("Habit chain updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Habit Chain", description = "Deletes a habit chain and cascades deletion to associated logs and invites.")
    public ResponseEntity<ApiResponse<Void>> deleteChain(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id
    ) {
        habitChainService.deleteChain(currentUser, id);
        return ResponseEntity.ok(ApiResponse.success("Habit chain deleted successfully", null));
    }
}
