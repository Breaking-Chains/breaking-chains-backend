package com.breakingchains.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "milestone_badges",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_chain_badge", columnNames = {"chain_id", "badge_type"})
    },
    indexes = {
        @Index(name = "idx_milestone_user", columnList = "user_id, achieved_at DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneBadge {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chain_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private HabitChain habitChain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;

    @CreationTimestamp
    @Column(name = "achieved_at", nullable = false, updatable = false)
    private LocalDateTime achievedAt;
}
