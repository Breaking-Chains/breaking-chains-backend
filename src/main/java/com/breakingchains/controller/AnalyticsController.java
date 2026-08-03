package com.breakingchains.controller;

import com.breakingchains.dto.AnalyticsResponse;
import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.MilestoneBadgeResponse;
import com.breakingchains.model.User;
import com.breakingchains.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Analytics & Milestones", description = "Endpoints for statistics, progress analytics, and badges")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/chains/{id}/analytics")
    @Operation(summary = "Get Chain Analytics", description = "Calculates clean percentage, streak statistics, common triggers, and historical breakdown.")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getChainAnalytics(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId
    ) {
        AnalyticsResponse response = analyticsService.getChainAnalytics(currentUser, chainId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/milestones")
    @Operation(summary = "Get User Milestone Badges", description = "Retrieves all milestone achievements (Day 3, 7, 21, 40, 90) and earned status for the user.")
    public ResponseEntity<ApiResponse<List<MilestoneBadgeResponse>>> getUserMilestones(
            @AuthenticationPrincipal User currentUser
    ) {
        List<MilestoneBadgeResponse> milestones = analyticsService.getUserMilestones(currentUser);
        return ResponseEntity.ok(ApiResponse.success(milestones));
    }
}
