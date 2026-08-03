package com.breakingchains.controller;

import com.breakingchains.dto.*;
import com.breakingchains.model.User;
import com.breakingchains.service.PartnerService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Mentorship & Accountability", description = "Endpoints for managing partnerships, chat messages, and counseling notes")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping("/chains/{id}/partners/invite")
    @Operation(summary = "Generate Partner Invite Code", description = "Generates a unique 6-character invitation code to connect an accountability partner or mentor to a chain.")
    public ResponseEntity<ApiResponse<InvitePartnerResponse>> createInvite(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId,
            @RequestBody InvitePartnerRequest request
    ) {
        InvitePartnerResponse response = partnerService.createInvite(currentUser, chainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Partner invite code generated successfully", response));
    }

    @PostMapping("/partners/accept")
    @Operation(summary = "Accept Partner Invite Code", description = "Accepts an accountability or mentorship invitation link using the invite code.")
    public ResponseEntity<ApiResponse<InvitePartnerResponse>> acceptInvite(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AcceptPartnerInviteRequest request
    ) {
        InvitePartnerResponse response = partnerService.acceptInvite(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Partnership accepted successfully", response));
    }

    @GetMapping("/partners/mentees")
    @Operation(summary = "Get Mentees List (For Mentors)", description = "Retrieves all habit chains where the authenticated user is an accepted mentor or accountability partner.")
    public ResponseEntity<ApiResponse<List<HabitChainResponse>>> getMentees(
            @AuthenticationPrincipal User currentUser
    ) {
        List<HabitChainResponse> mentees = partnerService.getMentees(currentUser);
        return ResponseEntity.ok(ApiResponse.success(mentees));
    }

    @PostMapping("/chains/{id}/counsel-notes")
    @Operation(summary = "Submit Mentor Counsel Note (*Nasiha*)", description = "Allows an accepted mentor to leave counsel notes on a mentee's habit chain.")
    public ResponseEntity<ApiResponse<CounselNoteResponse>> createCounselNote(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId,
            @Valid @RequestBody CounselNoteRequest request
    ) {
        CounselNoteResponse response = partnerService.createCounselNote(currentUser, chainId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Counsel note created successfully", response));
    }

    @GetMapping("/chains/{id}/counsel-notes")
    @Operation(summary = "Get Chain Counsel Notes", description = "Retrieves all counsel notes left on a specific habit chain.")
    public ResponseEntity<ApiResponse<List<CounselNoteResponse>>> getChainCounselNotes(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId
    ) {
        List<CounselNoteResponse> notes = partnerService.getChainCounselNotes(currentUser, chainId);
        return ResponseEntity.ok(ApiResponse.success(notes));
    }

    @PostMapping("/partnerships/{partnershipId}/messages")
    @Operation(summary = "Send 2-Way Mentorship Chat Message", description = "Sends a 2-way chat message inside a partnership/mentorship channel.")
    public ResponseEntity<ApiResponse<PartnerMessageResponse>> sendPartnerMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("partnershipId") UUID partnershipId,
            @Valid @RequestBody SendPartnerMessageRequest request
    ) {
        PartnerMessageResponse response = partnerService.sendPartnerMessage(currentUser, partnershipId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chat message sent successfully", response));
    }

    @GetMapping("/partnerships/{partnershipId}/messages")
    @Operation(summary = "Get 2-Way Mentorship Chat History", description = "Retrieves chat history messages for a partnership.")
    public ResponseEntity<ApiResponse<List<PartnerMessageResponse>>> getPartnershipMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("partnershipId") UUID partnershipId
    ) {
        List<PartnerMessageResponse> messages = partnerService.getPartnershipMessages(currentUser, partnershipId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/partners/distress-alert")
    @Operation(summary = "Send Partner Distress SOS Alert", description = "Dispatches an instant distress SOS notification to the designated partner for a habit chain.")
    public ResponseEntity<ApiResponse<Void>> notifyPartnerDistress(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody NotifyPartnerDistressRequest request
    ) {
        partnerService.notifyPartnerDistress(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Partner distress alert sent successfully", null));
    }
}
