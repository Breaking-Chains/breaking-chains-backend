package com.breakingchains.service;

import com.breakingchains.dto.*;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.AccountabilityPartnerRepository;
import com.breakingchains.repository.CounselNoteRepository;
import com.breakingchains.repository.HabitChainRepository;
import com.breakingchains.repository.PartnerMessageRepository;
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
    private final PartnerMessageRepository partnerMessageRepository;

    private void checkUser(User currentUser) {
        if (currentUser == null) {
            throw AppException.unauthorized("Authentication token is missing or invalid. Please log in first.");
        }
    }

    @Transactional
    public InvitePartnerResponse createInvite(User currentUser, UUID chainId, InvitePartnerRequest request) {
        checkUser(currentUser);

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
        checkUser(currentUser);

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
        checkUser(currentUser);

        log.debug("Fetching mentee habit chains for guide/mentor user ID: {}", currentUser.getId());
        List<AccountabilityPartner> partnerships = partnerRepository
                .findByPartnerUserIdAndStatus(currentUser.getId(), PartnershipStatus.ACCEPTED);

        return partnerships.stream()
                .map(p -> HabitChainResponse.fromEntity(p.getHabitChain()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CounselNoteResponse createCounselNote(User currentUser, UUID chainId, CounselNoteRequest request) {
        checkUser(currentUser);

        log.info("Attempting counsel note submission by user ID: {} for chain ID: {}", currentUser.getId(), chainId);

        HabitChain chain = habitChainRepository.findById(chainId)
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        boolean isChainOwner = chain.getUser().getId().equals(currentUser.getId());
        if (isChainOwner) {
            log.warn("Counsel note rejected - user ID: {} is the chain owner of chain ID: {}", currentUser.getId(), chainId);
            throw AppException.validationError("Counsel notes (Nasiha) can only be submitted by an accepted mentor or partner, not the chain owner.");
        }

        boolean isAcceptedMentor = partnerRepository.existsByHabitChainIdAndPartnerUserIdAndStatus(
                chainId, currentUser.getId(), PartnershipStatus.ACCEPTED);

        if (!isAcceptedMentor) {
            log.warn("Unauthorized counsel note attempt by user ID: {} for chain ID: {}", currentUser.getId(), chainId);
            throw AppException.unauthorized("You are not an accepted mentor for this habit chain.");
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
        log.info("Counsel note created successfully with ID: {} by mentor ID: {}", savedNote.getId(), currentUser.getId());

        return CounselNoteResponse.fromEntity(savedNote);
    }

    @Transactional(readOnly = true)
    public List<CounselNoteResponse> getChainCounselNotes(User currentUser, UUID chainId) {
        checkUser(currentUser);

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
    public PartnerMessageResponse sendPartnerMessage(User currentUser, UUID partnershipId, SendPartnerMessageRequest request) {
        checkUser(currentUser);

        AccountabilityPartner partnership = partnerRepository.findById(partnershipId)
                .orElseThrow(() -> AppException.notFound("Partnership not found"));

        if (partnership.getStatus() != PartnershipStatus.ACCEPTED) {
            throw AppException.validationError("Partnership is not active");
        }

        boolean isStudent = partnership.getUser().getId().equals(currentUser.getId());
        boolean isMentor = partnership.getPartnerUser() != null && partnership.getPartnerUser().getId().equals(currentUser.getId());

        if (!isStudent && !isMentor) {
            throw AppException.unauthorized("You are not a member of this mentorship partnership");
        }

        PartnerMessage message = PartnerMessage.builder()
                .partnership(partnership)
                .sender(currentUser)
                .messageContent(request.getMessageContent().trim())
                .isRead(false)
                .build();

        PartnerMessage savedMessage = partnerMessageRepository.save(message);
        log.info("Partner chat message sent successfully with ID: {} in partnership ID: {}", savedMessage.getId(), partnershipId);

        return PartnerMessageResponse.fromEntity(savedMessage);
    }

    @Transactional
    public List<PartnerMessageResponse> getPartnershipMessages(User currentUser, UUID partnershipId) {
        checkUser(currentUser);

        AccountabilityPartner partnership = partnerRepository.findById(partnershipId)
                .orElseThrow(() -> AppException.notFound("Partnership not found"));

        boolean isStudent = partnership.getUser().getId().equals(currentUser.getId());
        boolean isMentor = partnership.getPartnerUser() != null && partnership.getPartnerUser().getId().equals(currentUser.getId());

        if (!isStudent && !isMentor) {
            throw AppException.unauthorized("You are not a member of this mentorship partnership");
        }

        List<PartnerMessage> messages = partnerMessageRepository.findByPartnershipIdOrderByCreatedAtAsc(partnershipId);

        // Mark unread messages as read
        messages.stream()
                .filter(m -> !m.getSender().getId().equals(currentUser.getId()) && !m.isRead())
                .forEach(m -> m.setRead(true));

        return messages.stream()
                .map(PartnerMessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void notifyPartnerDistress(User currentUser, NotifyPartnerDistressRequest request) {
        checkUser(currentUser);

        log.info("ALERT: Distress notification triggered by user ID: {} for chain ID: {}", currentUser.getId(), request.getChainId());

        if (!habitChainRepository.existsByIdAndUserId(request.getChainId(), currentUser.getId())) {
            throw AppException.notFound("Habit chain not found");
        }

        // Log distress event for partner notification service
        log.info("Distress alert message: '{}'", request.getMessage() != null ? request.getMessage() : "Urge distress alert triggered");
    }
}
