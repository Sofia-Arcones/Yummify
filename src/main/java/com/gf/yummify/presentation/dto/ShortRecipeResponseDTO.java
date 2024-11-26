package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortRecipeResponseDTO {
    private UUID recipeId;
    private String image;
    private String title;
    private String description;
    private List<String> tags;
}
