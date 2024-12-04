package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.presentation.dto.FavoriteRecipeDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import com.gf.yummify.presentation.dto.ShortRecipeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);

    Recipe findRecipeById(UUID id);

    List<Recipe> findAllRecipes();

    void deleteRecipe(UUID id, Authentication authentication);

    void updateRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);

    RecipeResponseDTO getRecipeResponseDTO(UUID id);

    List<Recipe> findRecipesByUser(Authentication authentication);

    void addOrDeleteRecipeFavorite(Authentication authentication, UUID recipeId);

    Boolean findRecipeFavorite(Authentication authentication, UUID recipeId);

    Page<FavoriteRecipeDTO> findAllFavorites(Authentication authentication, int page, int size);

    Page<ShortRecipeResponseDTO> searchRecipes(String searchTerm, int page, int size);

    Page<ShortRecipeResponseDTO> findFilteredRecipes(int page, int size, Difficulty difficulty, Integer portions, List<String> tags, List<String> ingredients, IngredientType ingredientType);
}
