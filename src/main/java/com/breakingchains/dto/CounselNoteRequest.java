package com.breakingchains.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselNoteRequest {

    @NotBlank(message = "Counsel note content cannot be empty")
    @JsonProperty("counselText")
    private String noteContent;
}
