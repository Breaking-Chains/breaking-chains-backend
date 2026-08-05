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
@Table(name = "partnership_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnershipFeedback {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partnership_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AccountabilityPartner partnership;

    @Column(name = "feedback_type", nullable = false)
    private String feedbackType; // 'TERMINATION' or 'CANCELLATION'

    @Column(name = "reason_category", nullable = false)
    private String reasonCategory;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
