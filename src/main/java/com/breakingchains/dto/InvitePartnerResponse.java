package com.breakingchains.dto;

import com.breakingchains.model.PartnerRole;
import com.breakingchains.model.PartnershipStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitePartnerResponse {

    private UUID partnershipId;
    private UUID chainId;
    private String inviteCode;
    private PartnerRole role;
    private PartnershipStatus status;
    private LocalDateTime createdAt;
}
