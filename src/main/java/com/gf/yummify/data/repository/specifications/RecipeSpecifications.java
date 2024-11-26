package com.gf.yummify.data.repository.specifications;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.RecipeIngredient;
import com.gf.yummify.data.entity.Tag;
import com.gf.yummify.data.enums.Difficulty;
import com.gf.yummify.data.enums.IngredientType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecifications {
    public static Specification<Recipe> hasDifficulty(Difficulty difficulty) {
        return (root, query, criteriaBuilder) -> {
            if (difficulty == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("difficulty"), difficulty);
        };
    }

    public static Specification<Recipe> hasPortions(Integer portions) {
        return (root, query, criteriaBuilder) -> {
            if (portions == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("portions"), portions);
        };
    }

    public static Specification<Recipe> hasIngredientType(IngredientType ingredientType) {
        return (root, query, criteriaBuilder) -> {
            if (ingredientType == null) return criteriaBuilder.conjunction();
            Join<RecipeIngredient, Ingredient> ingredientsJoin = root.join("ingredients");
            return criteriaBuilder.equal(ingredientsJoin.get("ingredient").get("ingredientType"), ingredientType);
        };
    }

    public static Specification<Recipe> hasTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) return criteriaBuilder.conjunction();
            Join<Recipe, Tag> tagsJoin = root.join("tags");
            return tagsJoin.get("name").in(tags);
        };
    }

    public static Specification<Recipe> hasIngredients(List<String> ingredients) {
        return (root, query, criteriaBuilder) -> {
            if (ingredients == null || ingredients.isEmpty()) return criteriaBuilder.conjunction();
            Join<RecipeIngredient, Ingredient> ingredientsJoin = root.join("ingredients");
            return ingredientsJoin.get("ingredient").get("ingredientName").in(ingredients);
        };
    }
}
