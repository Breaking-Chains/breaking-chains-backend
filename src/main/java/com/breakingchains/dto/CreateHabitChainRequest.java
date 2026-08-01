package com.breakingchains.dto;

import com.breakingchains.model.HabitCategory;
import com.breakingchains.model.HabitSubCategory;
import com.breakingchains.model.PrivacyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHabitChainRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private HabitCategory category;

    private HabitSubCategory subCategory;

    private PrivacyLevel privacyLevel;

    private LocalDateTime targetStartDate;

    private BigDecimal costPerInstance;

    private Integer timeMinutesPerInstance;

    private List<String> triggerTags;

    private String substituteAction;

    private String intentStatement;
}
