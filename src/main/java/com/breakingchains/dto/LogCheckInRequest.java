package com.breakingchains.dto;

import com.breakingchains.model.CheckInStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogCheckInRequest {

    @NotNull(message = "Check-in status is required")
    private CheckInStatus status;

    private LocalDateTime logTimestamp;

    private Integer intensityLevel;

    private String triggerTag;

    private String reflectionNote;

    private String goodDeedDone;
}
