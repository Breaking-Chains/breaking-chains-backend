package com.breakingchains.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSlipGuidanceDto {

    private String title;
    private String subtitle;
    private String spiritualRemind;
    private String immediateAction;
    private String charitySuggestion;
    private String chaserEffectWarning;
    private String routineSwapSuggestion;
}
