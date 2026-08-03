package com.breakingchains.controller;

import com.breakingchains.dto.ApiResponse;
import com.breakingchains.dto.MentorProfileResponse;
import com.breakingchains.dto.MentorRegistrationRequest;
import com.breakingchains.dto.UpdateMentorStatusRequest;
import com.breakingchains.model.User;
import com.breakingchains.service.MentorService;
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
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
@Tag(name = "Mentor Directory", description = "Endpoints for registering and verifying spiritual mentors")
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/register")
    @Operation(summary = "Register as a Mentor", description = "Submits a registration application to become a verified mentor/counselor.")
    public ResponseEntity<ApiResponse<MentorProfileResponse>> registerMentor(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody MentorRegistrationRequest request) {
        MentorProfileResponse response = mentorService.registerMentor(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mentor registration application submitted successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get My Mentor Profile", description = "Retrieves the mentor profile details for the authenticated user.")
    public ResponseEntity<ApiResponse<MentorProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        MentorProfileResponse response = mentorService.getMyProfile(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/verified")
    @Operation(summary = "Get Verified Mentors Directory", description = "Retrieves all approved verified mentors (status: APPROVED).")
    public ResponseEntity<ApiResponse<List<MentorProfileResponse>>> getVerifiedMentors() {
        List<MentorProfileResponse> mentors = mentorService.getVerifiedMentors();
        return ResponseEntity.ok(ApiResponse.success(mentors));
    }

    @GetMapping("/applications")
    @Operation(summary = "Get All Mentor Applications (Admin / Dev)", description = "Retrieves all mentor applications (for admin/developer review).")
    public ResponseEntity<ApiResponse<List<MentorProfileResponse>>> getAllApplications() {
        List<MentorProfileResponse> applications = mentorService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @PutMapping("/applications/{profileId}/status")
    @Operation(summary = "Update Mentor Application Status (Admin / Dev)", description = "Approves or rejects a mentor registration application.")
    public ResponseEntity<ApiResponse<MentorProfileResponse>> updateApplicationStatus(
            @PathVariable UUID profileId,
            @Valid @RequestBody UpdateMentorStatusRequest request) {
        MentorProfileResponse response = mentorService.updateApplicationStatus(profileId, request);
        return ResponseEntity.ok(ApiResponse.success("Mentor application status updated successfully", response));
    }
}
