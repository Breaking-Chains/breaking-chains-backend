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
    name = "log_entries",
    indexes = {
        @Index(name = "idx_log_entries_chain_timestamp", columnList = "chain_id, log_timestamp DESC"),
        @Index(name = "idx_log_entries_user_timestamp", columnList = "user_id, log_timestamp DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

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

    @Column(name = "log_timestamp", nullable = false)
    private LocalDateTime logTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckInStatus status;

    @Column(name = "intensity_level")
    @Builder.Default
    private Integer intensityLevel = 1;

    @Column(name = "trigger_tag")
    private String triggerTag;

    @Column(name = "reflection_note", columnDefinition = "TEXT")
    private String reflectionNote;

    @Column(name = "good_deed_done", columnDefinition = "TEXT")
    private String goodDeedDone;

    @Column(name = "chaser_alert_active", nullable = false)
    @Builder.Default
    private Boolean chaserAlertActive = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
