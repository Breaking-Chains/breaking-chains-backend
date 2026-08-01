package com.breakingchains.dto;

import com.breakingchains.model.EmergencyType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContentResponse {

    private UUID sessionId;
    private UUID chainId;
    private EmergencyType sessionType;
    private String title;
    private String subtitle;
    private String immediatePhysicalStep;
    private String waterProtocolStep;
    private String spiritualShield;
    private Integer breathingTimerSeconds;
    private List<String> groundingSteps;
    private Integer cravingBefore;
    private LocalDateTime createdAt;
}
