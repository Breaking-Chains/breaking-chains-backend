package com.breakingchains.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendPartnerMessageRequest {

    @NotBlank(message = "Message content cannot be empty")
    private String messageContent;
}
