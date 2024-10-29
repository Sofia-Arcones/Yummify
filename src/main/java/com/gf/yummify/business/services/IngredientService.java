package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;

import java.util.List;

public interface IngredientService {
    Ingredient findByIngredientName(String ingredientName);

    List<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus);

    Ingredient findOrCreateIngredient(String name);

    List<IngredientAutocompleteDTO> getApprovedIngredientsForAutocomplete();
}
