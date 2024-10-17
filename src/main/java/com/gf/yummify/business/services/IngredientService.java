package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;

public interface IngredientService {
    Ingredient findByIngredientName(String ingredientName);
    Ingredient createIngredient(String ingredientName);
}
