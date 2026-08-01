package com.breakingchains.dto;

import com.breakingchains.model.EmergencySession;
import com.breakingchains.model.EmergencyType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencySessionResponse {

    private UUID id;
    private UUID chainId;
    private EmergencyType sessionType;
    private String techniqueUsed;
    private Integer cravingBefore;
    private Integer cravingAfter;
    private Integer durationSeconds;
    private LocalDateTime createdAt;

    public static EmergencySessionResponse fromEntity(EmergencySession session) {
        return EmergencySessionResponse.builder()
                .id(session.getId())
                .chainId(session.getHabitChain().getId())
                .sessionType(session.getSessionType())
                .techniqueUsed(session.getTechniqueUsed())
                .cravingBefore(session.getCravingBefore())
                .cravingAfter(session.getCravingAfter())
                .durationSeconds(session.getDurationSeconds())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
