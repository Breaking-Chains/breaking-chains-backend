package com.breakingchains.repository;

import com.breakingchains.model.EmergencySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmergencySessionRepository extends JpaRepository<EmergencySession, UUID> {

    List<EmergencySession> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<EmergencySession> findByHabitChainIdAndUserIdOrderByCreatedAtDesc(UUID chainId, UUID userId);

    Optional<EmergencySession> findByIdAndUserId(UUID id, UUID userId);
}
