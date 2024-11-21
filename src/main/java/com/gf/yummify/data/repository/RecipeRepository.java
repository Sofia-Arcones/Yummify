package com.gf.yummify.data.repository;

import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    List<Recipe> findByUser(User user);
}
