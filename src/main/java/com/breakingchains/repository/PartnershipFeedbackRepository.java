package com.breakingchains.repository;

import com.breakingchains.model.PartnershipFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnershipFeedbackRepository extends JpaRepository<PartnershipFeedback, UUID> {
}
