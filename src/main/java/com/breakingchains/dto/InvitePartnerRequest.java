package com.breakingchains.dto;

import com.breakingchains.model.PartnerRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitePartnerRequest {

    private PartnerRole role;
}
