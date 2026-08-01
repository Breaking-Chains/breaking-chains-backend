package com.breakingchains.service;

import com.breakingchains.dto.AnalyticsResponse;
import com.breakingchains.dto.MilestoneBadgeResponse;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.HabitChainRepository;
import com.breakingchains.repository.LogEntryRepository;
import com.breakingchains.repository.MilestoneBadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final HabitChainRepository habitChainRepository;
    private final LogEntryRepository logEntryRepository;
    private final MilestoneBadgeRepository milestoneBadgeRepository;

    private void checkUser(User currentUser) {
        if (currentUser == null) {
            throw AppException.unauthorized("Authentication token is missing or invalid. Please log in first.");
        }
    }

    @Transactional
    public AnalyticsResponse getChainAnalytics(User currentUser, UUID chainId) {
        checkUser(currentUser);

        log.info("Calculating analytics for user ID: {}, Chain ID: {}", currentUser.getId(), chainId);

        HabitChain chain = habitChainRepository.findByIdAndUserId(chainId, currentUser.getId())
                .orElseThrow(() -> AppException.notFound("Habit chain not found"));

        List<LogEntry> logs = logEntryRepository
                .findByHabitChainIdAndUserIdOrderByLogTimestampDesc(chainId, currentUser.getId());

        long totalDaysTracked = Math.max(1, ChronoUnit.DAYS.between(chain.getTargetStartDate(), LocalDateTime.now()) + 1);

        long totalCleanDays = logs.stream()
                .filter(l -> l.getStatus() == CheckInStatus.CLEAN || l.getStatus() == CheckInStatus.URGE_RESISTED)
                .count();

        long totalSlipUps = logs.stream()
                .filter(l -> l.getStatus() == CheckInStatus.SLIP_UP)
                .count();

        double cleanPercentage = Math.min(100.0, Math.round(((double) totalCleanDays / totalDaysTracked) * 100.0 * 100.0) / 100.0);

        Optional<LogEntry> lastSlip = logs.stream()
                .filter(l -> l.getStatus() == CheckInStatus.SLIP_UP)
                .findFirst();

        LocalDateTime lastSlipOrStart = lastSlip.isPresent() ? lastSlip.get().getLogTimestamp() : chain.getTargetStartDate();
        long currentStreakDays = Math.max(0, ChronoUnit.DAYS.between(lastSlipOrStart, LocalDateTime.now()));

        BigDecimal costPerInstance = chain.getCostPerInstance() != null ? chain.getCostPerInstance() : BigDecimal.ZERO;
        BigDecimal moneySaved = costPerInstance.multiply(BigDecimal.valueOf(totalCleanDays));

        int timeMinutes = chain.getTimeMinutesPerInstance() != null ? chain.getTimeMinutesPerInstance() : 0;
        double timeSavedHours = Math.round(((double) (timeMinutes * totalCleanDays) / 60.0) * 100.0) / 100.0;

        Map<String, Long> triggerBreakdown = logs.stream()
                .filter(l -> l.getTriggerTag() != null && !l.getTriggerTag().isBlank())
                .collect(Collectors.groupingBy(LogEntry::getTriggerTag, Collectors.counting()));

        // Evaluate & auto-award neuroplasticity / Nafs milestones
        checkAndAwardMilestones(currentUser, chain, currentStreakDays);

        List<MilestoneBadge> badges = milestoneBadgeRepository.findByHabitChainIdOrderByAchievedAtDesc(chainId);
        List<MilestoneBadgeResponse> milestoneResponses = badges.stream()
                .map(MilestoneBadgeResponse::fromEntity)
                .collect(Collectors.toList());

        return AnalyticsResponse.builder()
                .chainId(chain.getId())
                .title(chain.getTitle())
                .category(chain.getCategory())
                .subCategory(chain.getSubCategory())
                .totalDaysTracked(totalDaysTracked)
                .totalCleanDays(totalCleanDays)
                .totalSlipUps(totalSlipUps)
                .cleanPercentage(cleanPercentage)
                .currentStreakDays(currentStreakDays)
                .longestStreakDays(currentStreakDays)
                .moneySaved(moneySaved)
                .timeSavedHours(timeSavedHours)
                .sadaqahPotential(moneySaved)
                .triggerBreakdown(triggerBreakdown)
                .earnedMilestones(milestoneResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MilestoneBadgeResponse> getUserMilestones(User currentUser) {
        checkUser(currentUser);

        List<MilestoneBadge> badges = milestoneBadgeRepository.findByUserIdOrderByAchievedAtDesc(currentUser.getId());
        return badges.stream()
                .map(MilestoneBadgeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private void checkAndAwardMilestones(User user, HabitChain chain, long currentStreakDays) {
        if (currentStreakDays >= 3) {
            awardIfMissing(user, chain, BadgeType.NAFS_AMMARAH_SURVIVOR);
        }
        if (currentStreakDays >= 7) {
            awardIfMissing(user, chain, BadgeType.NAFS_LAWWAMAH_STRIVER);
        }
        if (currentStreakDays >= 21) {
            awardIfMissing(user, chain, BadgeType.NEURAL_REWIRE_21);
        }
        if (currentStreakDays >= 40) {
            awardIfMissing(user, chain, BadgeType.HEART_PURITY_40);
        }
        if (currentStreakDays >= 90) {
            awardIfMissing(user, chain, BadgeType.NAFS_MUTMAINNAH_RESET);
        }
    }

    private void awardIfMissing(User user, HabitChain chain, BadgeType badgeType) {
        if (!milestoneBadgeRepository.existsByHabitChainIdAndBadgeType(chain.getId(), badgeType)) {
            MilestoneBadge badge = MilestoneBadge.builder()
                    .habitChain(chain)
                    .user(user)
                    .badgeType(badgeType)
                    .build();
            milestoneBadgeRepository.save(badge);
            log.info("AWARDED MILESTONE: {} to user ID: {} for chain ID: {}", badgeType, user.getId(), chain.getId());
        }
    }
}
