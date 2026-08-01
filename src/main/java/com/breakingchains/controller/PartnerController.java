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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping("/chains/{id}/partners/invite")
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
    public ResponseEntity<ApiResponse<InvitePartnerResponse>> acceptInvite(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AcceptPartnerInviteRequest request
    ) {
        InvitePartnerResponse response = partnerService.acceptInvite(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Partnership accepted successfully", response));
    }

    @GetMapping("/partners/mentees")
    public ResponseEntity<ApiResponse<List<HabitChainResponse>>> getMentees(
            @AuthenticationPrincipal User currentUser
    ) {
        List<HabitChainResponse> mentees = partnerService.getMentees(currentUser);
        return ResponseEntity.ok(ApiResponse.success(mentees));
    }

    @PostMapping("/chains/{id}/counsel-notes")
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
    public ResponseEntity<ApiResponse<List<CounselNoteResponse>>> getChainCounselNotes(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID chainId
    ) {
        List<CounselNoteResponse> notes = partnerService.getChainCounselNotes(currentUser, chainId);
        return ResponseEntity.ok(ApiResponse.success(notes));
    }

    @PostMapping("/partnerships/{partnershipId}/messages")
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
    public ResponseEntity<ApiResponse<List<PartnerMessageResponse>>> getPartnershipMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("partnershipId") UUID partnershipId
    ) {
        List<PartnerMessageResponse> messages = partnerService.getPartnershipMessages(currentUser, partnershipId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/partners/distress-alert")
    public ResponseEntity<ApiResponse<Void>> notifyPartnerDistress(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody NotifyPartnerDistressRequest request
    ) {
        partnerService.notifyPartnerDistress(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("Partner distress alert sent successfully", null));
    }
}
