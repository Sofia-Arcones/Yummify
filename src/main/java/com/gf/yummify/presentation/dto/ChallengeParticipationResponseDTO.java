package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeParticipationResponseDTO {
    private String username;
    private RecipeResponseDTO recipe;
    private Boolean isWinner;
    private String formattedParticipationDate;
}
