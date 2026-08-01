package com.breakingchains.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifyPartnerDistressRequest {

    @NotNull(message = "Chain ID is required")
    private UUID chainId;

    private String message;
}
