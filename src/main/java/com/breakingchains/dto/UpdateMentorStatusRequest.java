package com.breakingchains.dto;

import com.breakingchains.model.MentorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMentorStatusRequest {

    @NotNull(message = "Status is required")
    private MentorStatus status;
}
