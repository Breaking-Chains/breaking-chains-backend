package com.breakingchains.repository;

import com.breakingchains.model.AccountabilityPartner;
import com.breakingchains.model.PartnershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountabilityPartnerRepository extends JpaRepository<AccountabilityPartner, UUID> {

    Optional<AccountabilityPartner> findByInviteCode(String inviteCode);

    List<AccountabilityPartner> findByUserId(UUID userId);

    List<AccountabilityPartner> findByPartnerUserIdAndStatus(UUID partnerUserId, PartnershipStatus status);

    boolean existsByHabitChainIdAndPartnerUserIdAndStatus(UUID chainId, UUID partnerUserId, PartnershipStatus status);

    Optional<AccountabilityPartner> findByHabitChainIdAndPartnerUserId(UUID chainId, UUID partnerUserId);
}
