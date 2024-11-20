package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDTO {
    private String title;
    private String description;
    private Integer winnerQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reward;
    private Boolean isFinished;
    private String formattedCreationDate;
    private String formattedLastModification;
    // private List<ChallengeParticipationResponseDTO> participations;
}
