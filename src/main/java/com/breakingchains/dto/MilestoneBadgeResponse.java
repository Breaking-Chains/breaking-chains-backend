package com.breakingchains.dto;

import com.breakingchains.model.BadgeType;
import com.breakingchains.model.MilestoneBadge;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneBadgeResponse {

    private UUID badgeId;
    private UUID chainId;
    private BadgeType badgeType;
    private String title;
    private String description;
    private LocalDateTime achievedAt;

    public static MilestoneBadgeResponse fromEntity(MilestoneBadge badge) {
        String title;
        String desc;

        switch (badge.getBadgeType()) {
            case NAFS_AMMARAH_SURVIVOR:
                title = "3-Day Withdrawal Survivor (Nafs al-Ammarah)";
                desc = "Survived the initial acute physical urge peak.";
                break;
            case NAFS_LAWWAMAH_STRIVER:
                title = "7-Day Dopamine Reset Foundation (Nafs al-Lawwamah)";
                desc = "1 week clean! Brain dopamine receptor sensitivity is restoring.";
                break;
            case NEURAL_REWIRE_21:
                title = "21-Day Neural Rewiring";
                desc = "3 weeks clean! New healthy neural pathway established.";
                break;
            case HEART_PURITY_40:
                title = "40-Day Heart Purification";
                desc = "40 days clean! High emotional self-control and spiritual clarity achieved.";
                break;
            case NAFS_MUTMAINNAH_RESET:
                title = "90-Day Complete Reboot (Nafs al-Mutma'innah)";
                desc = "90 days clean! Complete dopamine baseline reboot and tranquil soul state.";
                break;
            default:
                title = badge.getBadgeType().name();
                desc = "Milestone achieved.";
                break;
        }

        return MilestoneBadgeResponse.builder()
                .badgeId(badge.getId())
                .chainId(badge.getHabitChain().getId())
                .badgeType(badge.getBadgeType())
                .title(title)
                .description(desc)
                .achievedAt(badge.getAchievedAt())
                .build();
    }
}
