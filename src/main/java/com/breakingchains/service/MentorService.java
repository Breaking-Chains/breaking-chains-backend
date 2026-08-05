package com.breakingchains.service;

import com.breakingchains.dto.MentorProfileResponse;
import com.breakingchains.dto.MentorRegistrationRequest;
import com.breakingchains.dto.UpdateMentorStatusRequest;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.MentorProfile;
import com.breakingchains.model.MentorStatus;
import com.breakingchains.model.Role;
import com.breakingchains.model.User;
import com.breakingchains.repository.MentorProfileRepository;
import com.breakingchains.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;

    private void checkUser(User currentUser) {
        if (currentUser == null) {
            throw AppException.unauthorized("Authentication required to access mentor services.");
        }
    }

    @Transactional
    public MentorProfileResponse registerMentor(User currentUser, MentorRegistrationRequest request) {
        checkUser(currentUser);

        log.info("User ID: {} is submitting a mentor registration application", currentUser.getId());

        Optional<MentorProfile> existing = mentorProfileRepository.findByUserId(currentUser.getId());
        if (existing.isPresent()) {
            throw AppException.validationError("You already have an active mentor registration application (Status: " + existing.get().getStatus() + ")");
        }

        boolean autoApprove = Boolean.TRUE.equals(request.getAutoApprove());
        MentorStatus initialStatus = autoApprove ? MentorStatus.APPROVED : MentorStatus.PENDING;
        String inviteCode = "MENTOR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        MentorProfile profile = MentorProfile.builder()
                .user(currentUser)
                .qualification(request.getQualification().trim())
                .specialization(request.getSpecialization().trim())
                .yearsOfExperience(request.getYearsOfExperience())
                .organization(request.getOrganization() != null ? request.getOrganization().trim() : null)
                .bio(request.getBio().trim())
                .status(initialStatus)
                .inviteCode(inviteCode)
                .build();

        MentorProfile saved = mentorProfileRepository.save(profile);

        if (autoApprove) {
            currentUser.setIsVerifiedMentor(true);
            currentUser.setRole(Role.MENTOR);
            userRepository.save(currentUser);
            log.info("Dev Mode: User ID: {} automatically approved as verified mentor", currentUser.getId());
        }

        return MentorProfileResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public MentorProfileResponse getMyProfile(User currentUser) {
        checkUser(currentUser);

        return mentorProfileRepository.findByUserId(currentUser.getId())
                .map(MentorProfileResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MentorProfileResponse> getVerifiedMentors() {
        log.debug("Fetching all verified mentors");
        return mentorProfileRepository.findByStatus(MentorStatus.APPROVED).stream()
                .map(MentorProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MentorProfileResponse> getAllApplications() {
        log.debug("Fetching all mentor applications for admin/dev review");
        return mentorProfileRepository.findAll().stream()
                .map(MentorProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MentorProfileResponse updateApplicationStatus(UUID profileId, UpdateMentorStatusRequest request) {
        log.info("Updating mentor application ID: {} to status: {}", profileId, request.getStatus());

        MentorProfile profile = mentorProfileRepository.findById(profileId)
                .orElseThrow(() -> AppException.notFound("Mentor application not found"));

        profile.setStatus(request.getStatus());
        MentorProfile saved = mentorProfileRepository.save(profile);

        User user = profile.getUser();
        if (request.getStatus() == MentorStatus.APPROVED) {
            user.setIsVerifiedMentor(true);
            user.setRole(Role.MENTOR);
        } else {
            user.setIsVerifiedMentor(false);
            if (user.getRole() != Role.ADMIN) {
                user.setRole(Role.USER);
            }
        }
        userRepository.save(user);

        log.info("Successfully updated mentor status for user ID: {} to {}", user.getId(), request.getStatus());
        return MentorProfileResponse.fromEntity(saved);
    }
}
