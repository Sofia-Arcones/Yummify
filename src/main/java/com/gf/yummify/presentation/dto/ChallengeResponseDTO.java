package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDTO {
    private UUID challengeId;
    private String title;
    private String description;
    private Integer winnerQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reward;
    private Boolean isFinished;
    private String formattedCreationDate;
    private String formattedLastModification;
    private Page<ChallengeParticipationResponseDTO> participations;
}
