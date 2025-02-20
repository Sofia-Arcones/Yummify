package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {
}
