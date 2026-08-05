package com.breakingchains.dto;

import com.breakingchains.model.ChainStatus;
import com.breakingchains.model.HabitCategory;
import com.breakingchains.model.HabitChain;
import com.breakingchains.model.HabitSubCategory;
import com.breakingchains.model.PrivacyLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitChainResponse {

    private UUID id;
    private UUID userId;
    private UUID partnershipId;
    private String title;
    private String description;
    private HabitCategory category;
    private HabitSubCategory subCategory;
    private PrivacyLevel privacyLevel;
    private ChainStatus status;
    private LocalDateTime targetStartDate;
    private BigDecimal costPerInstance;
    private Integer timeMinutesPerInstance;
    private List<String> triggerTags;
    private String substituteAction;
    private String intentStatement;
    private long currentStreak;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HabitChainResponse fromEntity(HabitChain chain) {
        long defaultStreak = chain.getTargetStartDate() != null 
                ? Math.max(0, ChronoUnit.DAYS.between(chain.getTargetStartDate(), LocalDateTime.now()))
                : 0;
        return fromEntity(chain, defaultStreak);
    }

    public static HabitChainResponse fromEntity(HabitChain chain, long currentStreak) {
        return HabitChainResponse.builder()
                .id(chain.getId())
                .userId(chain.getUser().getId())
                .title(chain.getTitle())
                .description(chain.getDescription())
                .category(chain.getCategory())
                .subCategory(chain.getSubCategory())
                .privacyLevel(chain.getPrivacyLevel())
                .status(chain.getStatus())
                .targetStartDate(chain.getTargetStartDate())
                .costPerInstance(chain.getCostPerInstance())
                .timeMinutesPerInstance(chain.getTimeMinutesPerInstance())
                .triggerTags(chain.getTriggerTags())
                .substituteAction(chain.getSubstituteAction())
                .intentStatement(chain.getIntentStatement())
                .currentStreak(currentStreak)
                .createdAt(chain.getCreatedAt())
                .updatedAt(chain.getUpdatedAt())
                .build();
    }
}
