package com.breakingchains.service;

import com.breakingchains.dto.CompleteEmergencyRequest;
import com.breakingchains.dto.EmergencyContentResponse;
import com.breakingchains.dto.EmergencySessionResponse;
import com.breakingchains.dto.StartEmergencyRequest;
import com.breakingchains.exception.AppException;
import com.breakingchains.model.*;
import com.breakingchains.repository.EmergencySessionRepository;
import com.breakingchains.repository.HabitChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final HabitChainRepository habitChainRepository;
    private final EmergencySessionRepository emergencySessionRepository;

    private void checkUser(User currentUser) {
        if (currentUser == null) {
            throw AppException.unauthorized("Authentication token is missing or invalid. Please log in first.");
        }
    }

    @Transactional
    public EmergencyContentResponse startEmergencySession(User currentUser, StartEmergencyRequest request) {
        checkUser(currentUser);

        log.info("Starting emergency SOS session for user ID: {}, Chain ID: {}", currentUser.getId(), request.getChainId());

        HabitChain chain = habitChainRepository.findByIdAndUserId(request.getChainId(), currentUser.getId())
                .orElseThrow(() -> {
                    log.warn("Emergency SOS failed - chain ID: {} not found for user ID: {}", request.getChainId(), currentUser.getId());
                    return AppException.notFound("Habit chain not found");
                });

        EmergencyType sessionType = request.getSessionType() != null 
                ? request.getSessionType() 
                : (chain.getCategory() == HabitCategory.SPIRITUAL_MORAL ? EmergencyType.PHYSICAL_CIRCUIT_BREAKER : EmergencyType.PSYCHOLOGICAL);

        int cravingBefore = request.getCravingBefore() != null ? request.getCravingBefore() : 8;

        EmergencySession session = EmergencySession.builder()
                .habitChain(chain)
                .user(currentUser)
                .sessionType(sessionType)
                .cravingBefore(cravingBefore)
                .build();

        EmergencySession savedSession = emergencySessionRepository.save(session);
        log.info("Emergency session started with ID: {}", savedSession.getId());

        return generateEmergencyContent(savedSession, chain);
    }

    @Transactional
    public EmergencySessionResponse completeEmergencySession(User currentUser, UUID sessionId, CompleteEmergencyRequest request) {
        checkUser(currentUser);

        log.info("Completing emergency session ID: {} for user ID: {}", sessionId, currentUser.getId());

        EmergencySession session = emergencySessionRepository.findByIdAndUserId(sessionId, currentUser.getId())
                .orElseThrow(() -> AppException.notFound("Emergency session not found"));

        if (request.getCravingAfter() != null) {
            session.setCravingAfter(request.getCravingAfter());
        }
        if (request.getDurationSeconds() != null) {
            session.setDurationSeconds(request.getDurationSeconds());
        }
        if (request.getTechniqueUsed() != null) {
            session.setTechniqueUsed(request.getTechniqueUsed().trim());
        }

        EmergencySession updatedSession = emergencySessionRepository.save(session);
        log.info("Emergency session ID: {} completed. Craving drop: {} -> {}", 
                sessionId, session.getCravingBefore(), session.getCravingAfter());
        return EmergencySessionResponse.fromEntity(updatedSession);
    }

    @Transactional(readOnly = true)
    public List<EmergencySessionResponse> getUserEmergencyHistory(User currentUser) {
        checkUser(currentUser);

        List<EmergencySession> sessions = emergencySessionRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return sessions.stream()
                .map(EmergencySessionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private EmergencyContentResponse generateEmergencyContent(EmergencySession session, HabitChain chain) {
        List<String> grounding = List.of(
                "5 things you can physically see around you right now",
                "4 things you can physically feel or touch",
                "3 distinct sounds you can hear in your environment",
                "2 scents you can smell",
                "1 slow, deep diaphragmatic breath in"
        );

        if (chain.getSubCategory() == HabitSubCategory.PMO_RECOVERY || chain.getCategory() == HabitCategory.SPIRITUAL_MORAL) {
            return EmergencyContentResponse.builder()
                    .sessionId(session.getId())
                    .chainId(chain.getId())
                    .sessionType(session.getSessionType())
                    .title("🚨 BREAK THE LOOP — EMERGENCY INTERVENTION")
                    .subtitle("Urges peak over 15 to 20 minutes like a wave. Execute these physical circuit breakers right now.")
                    .immediatePhysicalStep("🚨 STEP 1: Stand up, leave your bed and current room immediately. Put your phone down.")
                    .waterProtocolStep("💧 STEP 2: Go to the bathroom and wash your face with cool water or perform a full Wudu.")
                    .spiritualShield("📖 SPIRITUAL SHIELD: Recite Ayat al-Kursi (2:255), Surah An-Nur verse 30 ('Tell believing men to lower their gaze and guard their chastity'), and say 'A'udhu billahi mina ash-shaytani ar-rajim'.")
                    .breathingTimerSeconds(60)
                    .groundingSteps(grounding)
                    .cravingBefore(session.getCravingBefore())
                    .createdAt(session.getCreatedAt())
                    .build();
        } else {
            return EmergencyContentResponse.builder()
                    .sessionId(session.getId())
                    .chainId(chain.getId())
                    .sessionType(session.getSessionType())
                    .title("🚨 EMERGENCY CRAVING INTERVENTION")
                    .subtitle("Take a step back. Craving intensity peaks and subsides over 15 minutes.")
                    .immediatePhysicalStep("STEP 1: Step away from your desk or current physical space.")
                    .waterProtocolStep("STEP 2: Drink a large glass of ice-cold water.")
                    .spiritualShield("Reflect on your Intent (Niyyah) statement: '" + (chain.getIntentStatement() != null ? chain.getIntentStatement() : "Building self-mastery") + "'.")
                    .breathingTimerSeconds(60)
                    .groundingSteps(grounding)
                    .cravingBefore(session.getCravingBefore())
                    .createdAt(session.getCreatedAt())
                    .build();
        }
    }
}
