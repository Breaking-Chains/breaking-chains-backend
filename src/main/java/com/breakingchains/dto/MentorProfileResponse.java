package com.breakingchains.dto;

import com.breakingchains.model.MentorProfile;
import com.breakingchains.model.MentorStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponse {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String username;
    private String avatarUrl;
    private String qualification;
    private String specialization;
    private Integer yearsOfExperience;
    private String organization;
    private String bio;
    private MentorStatus status;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public static MentorProfileResponse fromEntity(MentorProfile entity) {
        if (entity == null) return null;
        return MentorProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .fullName(entity.getUser().getFullName())
                .username(entity.getUser().getUsername())
                .avatarUrl(entity.getUser().getAvatarUrl())
                .qualification(entity.getQualification())
                .specialization(entity.getSpecialization())
                .yearsOfExperience(entity.getYearsOfExperience())
                .organization(entity.getOrganization())
                .bio(entity.getBio())
                .status(entity.getStatus())
                .isVerified(Boolean.TRUE.equals(entity.getUser().getIsVerifiedMentor()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
