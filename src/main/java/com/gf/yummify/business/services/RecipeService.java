package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface RecipeService {
    Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);

    Recipe findRecipeById(UUID id);

    void deleteRecipe(UUID id);
    Recipe updateRecipe(UUID id, RecipeRequestDTO requestDTO, Authentication authentication);
    RecipeResponseDTO getRecipeResponseDTO(UUID id);
}
