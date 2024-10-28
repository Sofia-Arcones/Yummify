package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.presentation.dto.RecipeIngredientDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface RecipeService {
    void saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);
    List<RecipeIngredient> createRecipeIngredients(List<RecipeIngredientDTO> ingredients);
    String joinInstrucions(List<String> instructionsList);
    List<String> getInstructions(Recipe recipe);
}
