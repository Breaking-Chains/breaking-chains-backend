package com.breakingchains.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "habit_chains",
    indexes = {
        @Index(name = "idx_habit_chains_user_status", columnList = "user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitChain {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HabitCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_category", nullable = false)
    @Builder.Default
    private HabitSubCategory subCategory = HabitSubCategory.GENERAL_HABIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level", nullable = false)
    @Builder.Default
    private PrivacyLevel privacyLevel = PrivacyLevel.LEVEL_0_PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ChainStatus status = ChainStatus.ACTIVE;

    @Column(name = "target_start_date", nullable = false)
    private LocalDateTime targetStartDate;

    @Column(name = "cost_per_instance", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costPerInstance = BigDecimal.ZERO;

    @Column(name = "time_minutes_per_instance")
    @Builder.Default
    private Integer timeMinutesPerInstance = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "habit_chain_triggers", joinColumns = @JoinColumn(name = "chain_id"))
    @Column(name = "trigger_tag")
    @Builder.Default
    private List<String> triggerTags = new ArrayList<>();

    @Column(name = "substitute_action", columnDefinition = "TEXT")
    private String substituteAction;

    @Column(name = "intent_statement", columnDefinition = "TEXT")
    private String intentStatement;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
