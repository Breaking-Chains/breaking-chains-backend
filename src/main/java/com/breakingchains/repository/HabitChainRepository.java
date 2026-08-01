package com.breakingchains.repository;

import com.breakingchains.model.ChainStatus;
import com.breakingchains.model.HabitChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabitChainRepository extends JpaRepository<HabitChain, UUID> {

    List<HabitChain> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<HabitChain> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, ChainStatus status);

    Optional<HabitChain> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
