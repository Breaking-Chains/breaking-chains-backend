package com.breakingchains.dto;

import com.breakingchains.model.AccountabilityPartner;
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
public class AccountabilityPartnershipResponse {

    private UUID id;
    private UUID chainId;
    private UUID userId;
    private UUID partnerUserId;
    private String partnerFullName;
    private String partnerUsername;
    private PartnerRole role;
    private PartnershipStatus status;
    private String inviteCode;
    private LocalDateTime createdAt;
    private LocalDateTime terminationRequestedAt;

    public static AccountabilityPartnershipResponse fromEntity(AccountabilityPartner partnership) {
        UUID partnerId = null;
        String partnerName = null;
        String partnerUser = null;

        if (partnership.getPartnerUser() != null) {
            partnerId = partnership.getPartnerUser().getId();
            partnerName = partnership.getPartnerUser().getFullName();
            partnerUser = partnership.getPartnerUser().getUsername();
        }

        return AccountabilityPartnershipResponse.builder()
                .id(partnership.getId())
                .chainId(partnership.getHabitChain().getId())
                .userId(partnership.getUser().getId())
                .partnerUserId(partnerId)
                .partnerFullName(partnerName)
                .partnerUsername(partnerUser)
                .role(partnership.getRole())
                .status(partnership.getStatus())
                .inviteCode(partnership.getInviteCode())
                .createdAt(partnership.getCreatedAt())
                .terminationRequestedAt(partnership.getTerminationRequestedAt())
                .build();
    }
}
