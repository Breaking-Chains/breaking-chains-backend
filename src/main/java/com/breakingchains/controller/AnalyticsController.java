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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/chains/{id}/analytics")
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getChainAnalytics(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId
    ) {
        AnalyticsResponse response = analyticsService.getChainAnalytics(currentUser, chainId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/milestones")
    public ResponseEntity<ApiResponse<List<MilestoneBadgeResponse>>> getUserMilestones(
            @AuthenticationPrincipal User currentUser
    ) {
        List<MilestoneBadgeResponse> milestones = analyticsService.getUserMilestones(currentUser);
        return ResponseEntity.ok(ApiResponse.success(milestones));
    }
}
