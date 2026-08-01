package com.breakingchains.service;

import com.breakingchains.dto.*;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.AccountabilityPartnerRepository;
import com.breakingchains.repository.CounselNoteRepository;
import com.breakingchains.repository.HabitChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final HabitChainRepository habitChainRepository;
    private final AccountabilityPartnerRepository partnerRepository;
    private final CounselNoteRepository counselNoteRepository;

    @Transactional
    public InvitePartnerResponse createInvite(User currentUser, UUID chainId, InvitePartnerRequest request) {
        log.info("Generating partner invite code for user ID: {}, Chain ID: {}", currentUser.getId(), chainId);

        HabitChain chain = habitChainRepository.findByIdAndUserId(chainId, currentUser.getId())
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        PartnerRole role = request.getRole() != null ? request.getRole() : PartnerRole.SPIRITUAL_MENTOR;
        String inviteCode = "SUHBAH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        AccountabilityPartner partnership = AccountabilityPartner.builder()
                .habitChain(chain)
                .user(currentUser)
                .role(role)
                .inviteCode(inviteCode)
                .status(PartnershipStatus.PENDING)
                .build();

        AccountabilityPartner saved = partnerRepository.save(partnership);
        log.info("Partner invite code generated successfully: {} for chain ID: {}", inviteCode, chainId);

        return InvitePartnerResponse.builder()
                .partnershipId(saved.getId())
                .chainId(chain.getId())
                .inviteCode(saved.getInviteCode())
                .role(saved.getRole())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public InvitePartnerResponse acceptInvite(User currentUser, AcceptPartnerInviteRequest request) {
        log.info("User ID: {} attempting to accept invite code: {}", currentUser.getId(), request.getInviteCode());

        AccountabilityPartner partnership = partnerRepository.findByInviteCode(request.getInviteCode().trim().toUpperCase())
                .orElseThrow(() -> AppException.notFound("Invalid or expired invite code"));

        if (partnership.getStatus() != PartnershipStatus.PENDING) {
            throw AppException.validationError("Invite code has already been used or revoked");
        }

        if (partnership.getUser().getId().equals(currentUser.getId())) {
            throw AppException.validationError("You cannot become a mentor/partner for your own habit chain");
        }

        partnership.setPartnerUser(currentUser);
        partnership.setStatus(PartnershipStatus.ACCEPTED);

        AccountabilityPartner updated = partnerRepository.save(partnership);
        log.info("Partnership accepted successfully! Mentor ID: {}, Student ID: {}, Chain ID: {}",
                currentUser.getId(), partnership.getUser().getId(), partnership.getHabitChain().getId());

        return InvitePartnerResponse.builder()
                .partnershipId(updated.getId())
                .chainId(updated.getHabitChain().getId())
                .inviteCode(updated.getInviteCode())
                .role(updated.getRole())
                .status(updated.getStatus())
                .createdAt(updated.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<HabitChainResponse> getMentees(User currentUser) {
        log.debug("Fetching mentee habit chains for guide/mentor user ID: {}", currentUser.getId());
        List<AccountabilityPartner> partnerships = partnerRepository
                .findByPartnerUserIdAndStatus(currentUser.getId(), PartnershipStatus.ACCEPTED);

        return partnerships.stream()
                .map(p -> HabitChainResponse.fromEntity(p.getHabitChain()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CounselNoteResponse createCounselNote(User currentUser, UUID chainId, CounselNoteRequest request) {
        log.info("Creating counsel note by mentor ID: {} for chain ID: {}", currentUser.getId(), chainId);

        HabitChain chain = habitChainRepository.findById(chainId)
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        boolean isChainOwner = chain.getUser().getId().equals(currentUser.getId());
        boolean isAcceptedMentor = partnerRepository.existsByHabitChainIdAndPartnerUserIdAndStatus(
                chainId, currentUser.getId(), PartnershipStatus.ACCEPTED);

        if (!isChainOwner && !isAcceptedMentor) {
            log.warn("Unauthorized counsel note attempt by user ID: {} for chain ID: {}", currentUser.getId(), chainId);
            throw AppException.unauthorized("You are not an authorized mentor for this habit chain");
        }

        if (chain.getPrivacyLevel() == PrivacyLevel.LEVEL_0_PRIVATE || chain.getPrivacyLevel() == PrivacyLevel.LEVEL_1_STREAK_ONLY) {
            log.warn("Counsel note blocked due to privacy settings for chain ID: {}", chainId);
            throw AppException.validationError("Chain privacy level does not permit mentor counsel notes");
        }

        CounselNote note = CounselNote.builder()
                .habitChain(chain)
                .mentorUser(currentUser)
                .user(chain.getUser())
                .noteContent(request.getNoteContent().trim())
                .build();

        CounselNote savedNote = counselNoteRepository.save(note);
        log.info("Counsel note created successfully with ID: {}", savedNote.getId());

        return CounselNoteResponse.fromEntity(savedNote);
    }

    @Transactional(readOnly = true)
    public List<CounselNoteResponse> getChainCounselNotes(User currentUser, UUID chainId) {
        HabitChain chain = habitChainRepository.findById(chainId)
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        boolean isChainOwner = chain.getUser().getId().equals(currentUser.getId());
        boolean isAcceptedMentor = partnerRepository.existsByHabitChainIdAndPartnerUserIdAndStatus(
                chainId, currentUser.getId(), PartnershipStatus.ACCEPTED);

        if (!isChainOwner && !isAcceptedMentor) {
            throw AppException.unauthorized("You are not authorized to view counsel notes for this habit chain");
        }

        List<CounselNote> notes = counselNoteRepository.findByHabitChainIdOrderByCreatedAtDesc(chainId);
        return notes.stream()
                .map(CounselNoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void notifyPartnerDistress(User currentUser, NotifyPartnerDistressRequest request) {
        log.info("ALERT: Distress notification triggered by user ID: {} for chain ID: {}", currentUser.getId(), request.getChainId());

        if (!habitChainRepository.existsByIdAndUserId(request.getChainId(), currentUser.getId())) {
            throw AppException.notFound("Habit chain not found");
        }

        // Log distress event for partner notification service
        log.info("Distress alert message: '{}'", request.getMessage() != null ? request.getMessage() : "Urge distress alert triggered");
    }
}
