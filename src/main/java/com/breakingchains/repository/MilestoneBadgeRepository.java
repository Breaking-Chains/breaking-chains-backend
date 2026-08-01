package com.breakingchains.repository;

import com.breakingchains.model.BadgeType;
import com.breakingchains.model.MilestoneBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneBadgeRepository extends JpaRepository<MilestoneBadge, UUID> {

    List<MilestoneBadge> findByHabitChainIdOrderByAchievedAtDesc(UUID chainId);

    List<MilestoneBadge> findByUserIdOrderByAchievedAtDesc(UUID userId);

    boolean existsByHabitChainIdAndBadgeType(UUID chainId, BadgeType badgeType);
}
