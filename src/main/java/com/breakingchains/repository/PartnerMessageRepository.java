package com.breakingchains.repository;

import com.breakingchains.model.PartnerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerMessageRepository extends JpaRepository<PartnerMessage, UUID> {

    List<PartnerMessage> findByPartnershipIdOrderByCreatedAtAsc(UUID partnershipId);
}
