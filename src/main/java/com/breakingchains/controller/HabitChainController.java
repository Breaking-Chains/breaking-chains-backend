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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chains")
@RequiredArgsConstructor
public class HabitChainController {

    private final HabitChainService habitChainService;

    @PostMapping
    public ResponseEntity<ApiResponse<HabitChainResponse>> createChain(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateHabitChainRequest request
    ) {
        HabitChainResponse response = habitChainService.createChain(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Habit chain created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HabitChainResponse>>> getUserChains(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) ChainStatus status
    ) {
        List<HabitChainResponse> chains = habitChainService.getUserChains(currentUser, status);
        return ResponseEntity.ok(ApiResponse.success(chains));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HabitChainResponse>> getChainById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id
    ) {
        HabitChainResponse response = habitChainService.getChainById(currentUser, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HabitChainResponse>> updateChain(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateHabitChainRequest request
    ) {
        HabitChainResponse response = habitChainService.updateChain(currentUser, id, request);
        return ResponseEntity.ok(ApiResponse.success("Habit chain updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChain(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id
    ) {
        habitChainService.deleteChain(currentUser, id);
        return ResponseEntity.ok(ApiResponse.success("Habit chain deleted successfully", null));
    }
}
