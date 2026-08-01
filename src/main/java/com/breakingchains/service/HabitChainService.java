package com.breakingchains.service;

import com.breakingchains.dto.CreateHabitChainRequest;
import com.breakingchains.dto.HabitChainResponse;
import com.breakingchains.dto.UpdateHabitChainRequest;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.AccountabilityPartnerRepository;
import com.breakingchains.repository.HabitChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitChainService {

    private final HabitChainRepository habitChainRepository;
    private final AccountabilityPartnerRepository partnerRepository;

    private void checkUser(User currentUser) {
        if (currentUser == null) {
            throw AppException.unauthorized("Authentication token is missing or invalid. Please log in first.");
        }
    }

    @Transactional
    public HabitChainResponse createChain(User currentUser, CreateHabitChainRequest request) {
        checkUser(currentUser);

        log.info("Creating new habit chain for user ID: {}, Title: '{}', Category: {}",
                currentUser.getId(), request.getTitle(), request.getCategory());

        LocalDateTime startDate = request.getTargetStartDate() != null 
                ? request.getTargetStartDate() 
                : LocalDateTime.now();

        HabitSubCategory subCategory = request.getSubCategory() != null 
                ? request.getSubCategory() 
                : HabitSubCategory.GENERAL_HABIT;

        PrivacyLevel privacy = request.getPrivacyLevel() != null 
                ? request.getPrivacyLevel() 
                : PrivacyLevel.LEVEL_0_PRIVATE;

        BigDecimal cost = request.getCostPerInstance() != null 
                ? request.getCostPerInstance() 
                : BigDecimal.ZERO;

        Integer minutes = request.getTimeMinutesPerInstance() != null 
                ? request.getTimeMinutesPerInstance() 
                : 0;

        List<String> triggers = request.getTriggerTags() != null 
                ? request.getTriggerTags() 
                : new ArrayList<>();

        HabitChain chain = HabitChain.builder()
                .user(currentUser)
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .category(request.getCategory())
                .subCategory(subCategory)
                .privacyLevel(privacy)
                .status(ChainStatus.ACTIVE)
                .targetStartDate(startDate)
                .costPerInstance(cost)
                .timeMinutesPerInstance(minutes)
                .triggerTags(triggers)
                .substituteAction(request.getSubstituteAction() != null ? request.getSubstituteAction().trim() : null)
                .intentStatement(request.getIntentStatement() != null ? request.getIntentStatement().trim() : null)
                .build();

        HabitChain savedChain = habitChainRepository.save(chain);
        log.info("Habit chain created successfully with ID: {} for user ID: {}", savedChain.getId(), currentUser.getId());
        return HabitChainResponse.fromEntity(savedChain);
    }

    @Transactional(readOnly = true)
    public List<HabitChainResponse> getUserChains(User currentUser, ChainStatus statusFilter) {
        checkUser(currentUser);

        log.debug("Fetching habit chains for user ID: {}, Status Filter: {}", currentUser.getId(), statusFilter);
        List<HabitChain> chains;
        if (statusFilter != null) {
            chains = habitChainRepository.findByUserIdAndStatusOrderByCreatedAtDesc(currentUser.getId(), statusFilter);
        } else {
            chains = habitChainRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        }
        return chains.stream()
                .map(HabitChainResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HabitChainResponse getChainById(User currentUser, UUID chainId) {
        checkUser(currentUser);

        log.debug("Fetching habit chain ID: {} for user ID: {}", chainId, currentUser.getId());
        HabitChain chain = habitChainRepository.findById(chainId)
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        boolean isChainOwner = chain.getUser().getId().equals(currentUser.getId());
        boolean isAcceptedPartner = partnerRepository.existsByHabitChainIdAndPartnerUserIdAndStatus(
                chainId, currentUser.getId(), PartnershipStatus.ACCEPTED);

        if (!isChainOwner && !isAcceptedPartner) {
            throw AppException.notFound("Habit chain not found");
        }

        if (!isChainOwner && chain.getPrivacyLevel() == PrivacyLevel.LEVEL_0_PRIVATE) {
            throw AppException.forbidden("User has configured this habit chain as confidential (LEVEL_0_PRIVATE)");
        }

        return HabitChainResponse.fromEntity(chain);
    }

    @Transactional
    public HabitChainResponse updateChain(User currentUser, UUID chainId, UpdateHabitChainRequest request) {
        checkUser(currentUser);

        log.info("Updating habit chain ID: {} for user ID: {}", chainId, currentUser.getId());
        HabitChain chain = habitChainRepository.findByIdAndUserId(chainId, currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Habit chain update failed - ID: {} not found for user ID: {}", chainId, currentUser.getId());
                    return AppException.notFound("Habit chain not found");
                });

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            chain.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            chain.setDescription(request.getDescription().trim());
        }
        if (request.getCategory() != null) {
            chain.setCategory(request.getCategory());
        }
        if (request.getSubCategory() != null) {
            chain.setSubCategory(request.getSubCategory());
        }
        if (request.getPrivacyLevel() != null) {
            chain.setPrivacyLevel(request.getPrivacyLevel());
        }
        if (request.getStatus() != null) {
            chain.setStatus(request.getStatus());
        }
        if (request.getTargetStartDate() != null) {
            chain.setTargetStartDate(request.getTargetStartDate());
        }
        if (request.getCostPerInstance() != null) {
            chain.setCostPerInstance(request.getCostPerInstance());
        }
        if (request.getTimeMinutesPerInstance() != null) {
            chain.setTimeMinutesPerInstance(request.getTimeMinutesPerInstance());
        }
        if (request.getTriggerTags() != null) {
            chain.setTriggerTags(request.getTriggerTags());
        }
        if (request.getSubstituteAction() != null) {
            chain.setSubstituteAction(request.getSubstituteAction().trim());
        }
        if (request.getIntentStatement() != null) {
            chain.setIntentStatement(request.getIntentStatement().trim());
        }

        HabitChain updatedChain = habitChainRepository.save(chain);
        log.info("Habit chain ID: {} updated successfully", chainId);
        return HabitChainResponse.fromEntity(updatedChain);
    }

    @Transactional
    public void deleteChain(User currentUser, UUID chainId) {
        checkUser(currentUser);

        log.info("Deleting habit chain ID: {} for user ID: {}", chainId, currentUser.getId());
        HabitChain chain = habitChainRepository.findByIdAndUserId(chainId, currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Habit chain deletion failed - ID: {} not found for user ID: {}", chainId, currentUser.getId());
                    return AppException.notFound("Habit chain not found");
                });
        habitChainRepository.delete(chain);
        log.info("Habit chain ID: {} deleted successfully", chainId);
    }
}
