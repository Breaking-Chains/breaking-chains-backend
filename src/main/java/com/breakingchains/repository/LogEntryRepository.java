package com.breakingchains.repository;

import com.breakingchains.model.CheckInStatus;
import com.breakingchains.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    List<LogEntry> findByHabitChainIdAndUserIdOrderByLogTimestampDesc(UUID chainId, UUID userId);

    List<LogEntry> findByHabitChainIdOrderByLogTimestampDesc(UUID chainId);

    Optional<LogEntry> findByIdAndUserId(UUID id, UUID userId);

    long countByHabitChainIdAndStatus(UUID chainId, CheckInStatus status);

    long countByHabitChainId(UUID chainId);

    Optional<LogEntry> findTopByHabitChainIdAndStatusOrderByLogTimestampDesc(UUID chainId, CheckInStatus status);

    Optional<LogEntry> findTopByHabitChainIdAndUserIdOrderByLogTimestampDesc(UUID chainId, UUID userId);
}
