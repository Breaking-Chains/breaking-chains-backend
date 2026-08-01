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
    name = "accountability_partnerships",
    indexes = {
        @Index(name = "idx_partner_code", columnList = "invite_code", unique = true),
        @Index(name = "idx_partner_user_status", columnList = "partner_user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountabilityPartner {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User partnerUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerRole role;

    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PartnershipStatus status = PartnershipStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
