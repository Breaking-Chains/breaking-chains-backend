package com.breakingchains.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorRegistrationRequest {

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    private String organization;

    @NotBlank(message = "Bio / statement of purpose is required")
    private String bio;

    // Optional dev auto-approval flag for instant testing
    @Builder.Default
    private Boolean autoApprove = false;
}
