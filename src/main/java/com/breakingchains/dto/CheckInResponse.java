package com.breakingchains.dto;

import com.breakingchains.model.CheckInStatus;
import com.breakingchains.model.LogEntry;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInResponse {

    private UUID id;
    private UUID chainId;
    private UUID userId;
    private LocalDateTime logTimestamp;
    private CheckInStatus status;
    private Integer intensityLevel;
    private String triggerTag;
    private String reflectionNote;
    private String goodDeedDone;
    private Boolean chaserAlertActive;

    // Resilience Math Metrics
    private long currentStreakDays;
    private long longestStreakDays;
    private long totalCleanDays;
    private long totalDays;
    private double resilienceScore;

    // Category-specific Guidance Payload
    private PostSlipGuidanceDto postSlipGuidance;
    private LocalDateTime createdAt;

    public static CheckInResponse fromEntity(LogEntry logEntry) {
        return CheckInResponse.builder()
                .id(logEntry.getId())
                .chainId(logEntry.getHabitChain().getId())
                .userId(logEntry.getUser().getId())
                .logTimestamp(logEntry.getLogTimestamp())
                .status(logEntry.getStatus())
                .intensityLevel(logEntry.getIntensityLevel())
                .triggerTag(logEntry.getTriggerTag())
                .reflectionNote(logEntry.getReflectionNote())
                .goodDeedDone(logEntry.getGoodDeedDone())
                .chaserAlertActive(logEntry.getChaserAlertActive())
                .createdAt(logEntry.getCreatedAt())
                .build();
    }
}
