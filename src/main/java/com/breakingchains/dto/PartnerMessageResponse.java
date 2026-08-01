package com.breakingchains.dto;

import com.breakingchains.model.PartnerMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerMessageResponse {

    private UUID id;
    private UUID partnershipId;
    private UUID senderId;
    private String senderFullName;
    private String senderUsername;
    private String messageContent;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static PartnerMessageResponse fromEntity(PartnerMessage message) {
        return PartnerMessageResponse.builder()
                .id(message.getId())
                .partnershipId(message.getPartnership().getId())
                .senderId(message.getSender().getId())
                .senderFullName(message.getSender().getFullName())
                .senderUsername(message.getSender().getUsername())
                .messageContent(message.getMessageContent())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
