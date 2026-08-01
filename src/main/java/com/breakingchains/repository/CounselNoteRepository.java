package com.breakingchains.repository;

import com.breakingchains.model.CounselNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CounselNoteRepository extends JpaRepository<CounselNote, UUID> {

    List<CounselNote> findByHabitChainIdOrderByCreatedAtDesc(UUID chainId);

    List<CounselNote> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
