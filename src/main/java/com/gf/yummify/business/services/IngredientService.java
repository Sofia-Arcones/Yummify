package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;

import java.util.List;
import java.util.UUID;

public interface IngredientService {
    Ingredient findByIngredientName(String ingredientName);

    List<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus);

    Ingredient findOrCreateIngredient(String name);

    List<IngredientAutocompleteDTO> getApprovedIngredientsForAutocomplete();
    Ingredient findIngredientById(UUID id);
    Ingredient updateIngredient(Ingredient ingredient);
    List<Ingredient> findIngredientsByStatusAndType(IngredientStatus ingredientStatus, IngredientType ingredientType);
    List<Ingredient> findIngredientsByType(IngredientType ingredientType);
    String deleteIngredient(UUID id);

}
