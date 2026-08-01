package com.breakingchains.dto;

import com.breakingchains.model.HabitCategory;
import com.breakingchains.model.HabitSubCategory;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private UUID chainId;
    private String title;
    private HabitCategory category;
    private HabitSubCategory subCategory;
    private long totalDaysTracked;
    private long totalCleanDays;
    private long totalSlipUps;
    private double cleanPercentage;
    private long currentStreakDays;
    private long longestStreakDays;
    private BigDecimal moneySaved;
    private double timeSavedHours;
    private BigDecimal sadaqahPotential;
    private Map<String, Long> triggerBreakdown;
    private List<MilestoneBadgeResponse> earnedMilestones;
}
