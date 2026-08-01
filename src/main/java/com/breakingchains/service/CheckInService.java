package com.breakingchains.service;

import com.breakingchains.dto.CheckInResponse;
import com.breakingchains.dto.LogCheckInRequest;
import com.breakingchains.dto.PostSlipGuidanceDto;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.HabitChainRepository;
import com.breakingchains.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInService {

    private final HabitChainRepository habitChainRepository;
    private final LogEntryRepository logEntryRepository;

    @Transactional
    public CheckInResponse logCheckIn(User currentUser, UUID chainId, LogCheckInRequest request) {
        log.info("Logging check-in for user ID: {}, Chain ID: {}, Status: {}",
                currentUser.getId(), chainId, request.getStatus());

        HabitChain chain = habitChainRepository.findByIdAndUserId(chainId, currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Check-in failed - chain ID: {} not found for user ID: {}", chainId, currentUser.getId());
                    return AppException.notFound("Habit chain not found");
                });

        LocalDateTime timestamp = request.getLogTimestamp() != null 
                ? request.getLogTimestamp() 
                : LocalDateTime.now();

        boolean isSlip = request.getStatus() == CheckInStatus.SLIP_UP;

        // Check if a slip occurred within the last 48 hours for Chaser Effect warning
        Optional<LogEntry> lastSlip = logEntryRepository
                .findTopByHabitChainIdAndStatusOrderByLogTimestampDesc(chainId, CheckInStatus.SLIP_UP);

        boolean chaserActive = isSlip || (lastSlip.isPresent() &&
                Duration.between(lastSlip.get().getLogTimestamp(), timestamp).toHours() <= 48);

        LogEntry logEntry = LogEntry.builder()
                .habitChain(chain)
                .user(currentUser)
                .logTimestamp(timestamp)
                .status(request.getStatus())
                .intensityLevel(request.getIntensityLevel() != null ? request.getIntensityLevel() : 1)
                .triggerTag(request.getTriggerTag() != null ? request.getTriggerTag().trim() : null)
                .reflectionNote(request.getReflectionNote() != null ? request.getReflectionNote().trim() : null)
                .goodDeedDone(request.getGoodDeedDone() != null ? request.getGoodDeedDone().trim() : null)
                .chaserAlertActive(chaserActive)
                .build();

        LogEntry savedEntry = logEntryRepository.save(logEntry);
        log.info("Check-in logged successfully with ID: {} for chain ID: {}", savedEntry.getId(), chainId);

        CheckInResponse response = CheckInResponse.fromEntity(savedEntry);
        populateResilienceMetrics(chain, response);

        if (isSlip) {
            response.setPostSlipGuidance(generatePostSlipGuidance(chain));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getChainLogs(User currentUser, UUID chainId) {
        log.debug("Fetching check-in logs for chain ID: {} and user ID: {}", chainId, currentUser.getId());
        if (!habitChainRepository.existsByIdAndUserId(chainId, currentUser.getId())) {
            throw AppException.notFound("Habit chain not found");
        }

        List<LogEntry> entries = logEntryRepository
                .findByHabitChainIdAndUserIdOrderByLogTimestampDesc(chainId, currentUser.getId());

        return entries.stream()
                .map(CheckInResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLog(User currentUser, UUID chainId, UUID logId) {
        log.info("Deleting check-in log ID: {} for chain ID: {} and user ID: {}", logId, chainId, currentUser.getId());
        LogEntry logEntry = logEntryRepository.findByIdAndUserId(logId, currentUser.getId())
                .orElseThrow(() -> AppException.notFound("Log entry not found"));

        if (!logEntry.getHabitChain().getId().equals(chainId)) {
            throw AppException.notFound("Log entry does not belong to specified habit chain");
        }

        logEntryRepository.delete(logEntry);
        log.info("Check-in log ID: {} deleted successfully", logId);
    }

    private void populateResilienceMetrics(HabitChain chain, CheckInResponse response) {
        long totalDays = Math.max(1, ChronoUnit.DAYS.between(chain.getTargetStartDate(), LocalDateTime.now()) + 1);
        long cleanLogsCount = logEntryRepository.countByHabitChainIdAndStatus(chain.getId(), CheckInStatus.CLEAN)
                + logEntryRepository.countByHabitChainIdAndStatus(chain.getId(), CheckInStatus.URGE_RESISTED);

        double resilienceScore = Math.round(((double) cleanLogsCount / totalDays) * 100.0 * 100.0) / 100.0;
        resilienceScore = Math.min(100.0, resilienceScore);

        Optional<LogEntry> lastSlip = logEntryRepository
                .findTopByHabitChainIdAndStatusOrderByLogTimestampDesc(chain.getId(), CheckInStatus.SLIP_UP);

        LocalDateTime lastSlipOrStart = lastSlip.isPresent() 
                ? lastSlip.get().getLogTimestamp() 
                : chain.getTargetStartDate();

        long currentStreak = Math.max(0, ChronoUnit.DAYS.between(lastSlipOrStart, LocalDateTime.now()));

        response.setTotalDays(totalDays);
        response.setTotalCleanDays(cleanLogsCount);
        response.setResilienceScore(resilienceScore);
        response.setCurrentStreakDays(currentStreak);
        response.setLongestStreakDays(Math.max(currentStreak, response.getLongestStreakDays()));
    }

    private PostSlipGuidanceDto generatePostSlipGuidance(HabitChain chain) {
        if (chain.getSubCategory() == HabitSubCategory.PMO_RECOVERY || chain.getCategory() == HabitCategory.SPIRITUAL_MORAL) {
            return PostSlipGuidanceDto.builder()
                    .title("Renew Your Intent (Niyyah) & Stand Up Immediately")
                    .subtitle("A slip is a temporary detour, not an identity collapse. Turn to Allah right now with hope.")
                    .spiritualRemind("O My servants who have transgressed against themselves, do not despair of the mercy of Allah. Indeed, Allah forgives all sins. (Surah Az-Zumar 39:53)")
                    .immediateAction("1. Leave your bed/room immediately.\n2. Perform Wudu with cool water.\n3. Pray 2 Raka'at Salat al-Tawbah.")
                    .charitySuggestion("Donate $1 to $5 as Sadaqah to erase this mistake with a good deed (Al-Hasanat yudhibna al-sayyi'at).")
                    .chaserEffectWarning("⚠️ 48-Hour Chaser-Effect Caution: Dopamine levels are depleted right now. Urges will peak over the next 48 hours. Keep your environment clean and leave your phone outside your bedroom tonight.")
                    .routineSwapSuggestion(chain.getSubstituteAction() != null ? "Substitute Routine: " + chain.getSubstituteAction() : null)
                    .build();
        } else {
            return PostSlipGuidanceDto.builder()
                    .title("Analyze the Trigger & Re-Center")
                    .subtitle("Identify what caused this slip and execute your substitute routine immediately.")
                    .spiritualRemind("Self-discipline is built one decision at a time.")
                    .immediateAction("Change your environment and take 10 deep diaphragmatic breaths.")
                    .charitySuggestion(null)
                    .chaserEffectWarning("Stay mindful of stress and fatigue triggers over the next 24 hours.")
                    .routineSwapSuggestion(chain.getSubstituteAction() != null ? "Substitute Routine: " + chain.getSubstituteAction() : "Execute your substitute habit now.")
                    .build();
        }
    }
}
