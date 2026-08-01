package com.breakingchains.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselNoteRequest {

    @NotBlank(message = "Counsel note content cannot be empty")
    private String noteContent;
}
