package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeParticipationResponseDTO {
    private String username;
    private UUID recipeId;
    private Boolean isWinner;
    private String formattedParticipationDate;
}
