package com.gf.yummify.business.services;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.User;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.presentation.dto.IngredientAutocompleteDTO;
import com.gf.yummify.presentation.dto.IngredientRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface IngredientService {
    Ingredient findByIngredientName(String ingredientName);

    Ingredient findOrCreateIngredient(String name, User user);

    List<IngredientAutocompleteDTO> getApprovedIngredientsForAutocomplete();

    Ingredient findIngredientById(UUID id);

    Ingredient updateIngredient(IngredientRequestDTO ingredientRequestDTO, Authentication authentication);

    Page<Ingredient> findIngredientsByStatus(IngredientStatus ingredientStatus, int page, int size);

    Page<Ingredient> findIngredientsByStatusAndType(IngredientStatus ingredientStatus, IngredientType ingredientType, int page, int size);

    Page<Ingredient> findIngredientsByType(IngredientType ingredientType, int page, int size);

    Page<Ingredient> findAllIngredients(int page, int size);

}
