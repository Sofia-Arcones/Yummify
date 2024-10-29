package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Optional<Ingredient> findByIngredientName(String ingredientName);
    Boolean existsByIngredientName(String ingredientName);
    List<Ingredient> findByIngredientStatus(IngredientStatus ingredientStatus);
}
