package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.FavoriteRecipe;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);

    Recipe findRecipeById(UUID id);

    void deleteRecipe(UUID id);

    RecipeResponseDTO getRecipeResponseDTO(UUID id);

    List<Recipe> findRecipesByUser(Authentication authentication);

    void addOrDeleteRecipeFavorite(Authentication authentication, UUID recipeId);

    Boolean findRecipeFavorite(Authentication authentication, UUID recipeId);

    Page<FavoriteRecipe> findAllFavorites(Authentication authentication, int page, int size);
}
