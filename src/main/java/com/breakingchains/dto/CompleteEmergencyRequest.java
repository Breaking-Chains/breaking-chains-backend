package com.breakingchains.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteEmergencyRequest {

    private Integer cravingAfter;
    private Integer durationSeconds;
    private String techniqueUsed;
}
