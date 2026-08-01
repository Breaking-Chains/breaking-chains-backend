package com.breakingchains.dto;

import com.breakingchains.model.ChainStatus;
import com.breakingchains.model.HabitCategory;
import com.breakingchains.model.HabitSubCategory;
import com.breakingchains.model.PrivacyLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHabitChainRequest {

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
}
