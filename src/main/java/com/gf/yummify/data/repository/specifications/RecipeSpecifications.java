package com.gf.yummify.data.repository.specifications;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.data.entity.Tag;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecifications {

    // Filtro por dificultad
    public static Specification<Recipe> hasDifficulty(Difficulty difficulty) {
        return (root, query, criteriaBuilder) -> {
            if (difficulty == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("difficulty"), difficulty);
        };
    }

    // Filtro por porciones
    public static Specification<Recipe> hasPortions(Integer portions) {
        return (root, query, criteriaBuilder) -> {
            if (portions == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("portions"), portions);
        };
    }

    // Filtro por tipo de ingrediente
    public static Specification<Recipe> hasIngredientType(IngredientType ingredientType) {
        return (root, query, criteriaBuilder) -> {
            if (ingredientType == null) return criteriaBuilder.conjunction();
            Join<RecipeIngredient, Ingredient> ingredientsJoin = root.join("ingredients");
            return criteriaBuilder.equal(ingredientsJoin.get("ingredient").get("ingredientType"), ingredientType);
        };
    }

    // Filtro por etiquetas
    public static Specification<Recipe> hasTags(String tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.trim().isEmpty()) return criteriaBuilder.conjunction();
            Join<Recipe, Tag> tagsJoin = root.join("tags");
            return criteriaBuilder.like(criteriaBuilder.lower(tagsJoin.get("name")), "%" + tags.toLowerCase() + "%");
        };
    }

    // Filtro por ingredientes
    public static Specification<Recipe> hasIngredients(String ingredients) {
        return (root, query, criteriaBuilder) -> {
            if (ingredients == null || ingredients.trim().isEmpty()) return criteriaBuilder.conjunction();
            Join<RecipeIngredient, Ingredient> ingredientsJoin = root.join("ingredients");
            return criteriaBuilder.like(criteriaBuilder.lower(ingredientsJoin.get("ingredient").get("ingredientName")),
                    "%" + ingredients.toLowerCase() + "%");
        };
    }
}
