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
    name = "emergency_sessions",
    indexes = {
        @Index(name = "idx_emergency_user_created", columnList = "user_id, created_at DESC"),
        @Index(name = "idx_emergency_chain_created", columnList = "chain_id, created_at DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencySession {

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
    @Column(name = "session_type", nullable = false)
    private EmergencyType sessionType;

    @Column(name = "technique_used")
    private String techniqueUsed;

    @Column(name = "craving_before")
    private Integer cravingBefore;

    @Column(name = "craving_after")
    private Integer cravingAfter;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
