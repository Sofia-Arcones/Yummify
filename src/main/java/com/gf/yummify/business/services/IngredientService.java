package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;

import java.util.List;

public interface IngredientService {
    Ingredient findByIngredientName(String ingredientName);
    Ingredient createIngredient(String ingredientName);
    List<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus);
}
