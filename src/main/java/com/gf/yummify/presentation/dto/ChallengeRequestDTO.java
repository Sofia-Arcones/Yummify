package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeRequestDTO {
    private String title;
    private String description;
    private Integer winnerQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reward;
}
