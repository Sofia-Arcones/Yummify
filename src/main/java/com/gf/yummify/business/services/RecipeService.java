package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.presentation.dto.RecipeIngredientDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    Recipe saveRecipe(RecipeRequestDTO recipeRequestDTO, Authentication authentication);
}
