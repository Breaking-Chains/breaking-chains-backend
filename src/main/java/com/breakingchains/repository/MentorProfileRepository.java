package com.breakingchains.repository;

import com.breakingchains.model.MentorProfile;
import com.breakingchains.model.MentorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, UUID> {
    Optional<MentorProfile> findByUserId(UUID userId);
    List<MentorProfile> findByStatus(MentorStatus status);
    boolean existsByUserId(UUID userId);
}
