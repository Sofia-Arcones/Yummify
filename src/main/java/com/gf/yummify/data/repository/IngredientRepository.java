package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Optional<Ingredient> findByIngredientName(String ingredientName);

    Boolean existsByIngredientName(String ingredientName);

    Page<Ingredient> findByIngredientStatus(IngredientStatus ingredientStatus, Pageable pageable);

    List<Ingredient> findByIngredientStatus(IngredientStatus ingredientStatus);

    Page<Ingredient> findByIngredientType(IngredientType ingredientType, Pageable pageable);

    Page<Ingredient> findByIngredientStatusAndIngredientType(IngredientStatus ingredientStatus, IngredientType ingredientType, Pageable pageable);
}
