package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.FavoriteRecipe;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, UUID> {
    Optional<FavoriteRecipe> findByUserAndRecipe(User user, Recipe recipe);

    Page<FavoriteRecipe> findByUser(User user, Pageable pageable);
}
