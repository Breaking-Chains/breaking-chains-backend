package com.breakingchains.dto;

import com.breakingchains.model.CounselNote;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselNoteResponse {

    private UUID id;
    private UUID chainId;
    private UUID mentorId;
    private String mentorFullName;
    private String mentorUsername;

    @JsonProperty("counselText")
    private String noteContent;

    private LocalDateTime createdAt;

    public static CounselNoteResponse fromEntity(CounselNote note) {
        return CounselNoteResponse.builder()
                .id(note.getId())
                .chainId(note.getHabitChain().getId())
                .mentorId(note.getMentorUser().getId())
                .mentorFullName(note.getMentorUser().getFullName())
                .mentorUsername(note.getMentorUser().getUsername())
                .noteContent(note.getNoteContent())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
