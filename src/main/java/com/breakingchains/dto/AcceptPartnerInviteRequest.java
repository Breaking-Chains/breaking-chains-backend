package com.breakingchains.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptPartnerInviteRequest {

    @NotBlank(message = "Invite code is required")
    private String inviteCode;
}
