package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.FavoriteRecipe;
import com.gf.yummify.data.entity.Recipe;
import com.gf.yummify.data.entity.Tag;
import com.gf.yummify.presentation.dto.FavoriteRecipeDTO;
import com.gf.yummify.presentation.dto.RecipeRequestDTO;
import com.gf.yummify.presentation.dto.RecipeResponseDTO;
import com.gf.yummify.presentation.dto.ShortRecipeResponseDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface RecipeMapper {
    @Mapping(target = "recipeId", source = "favoriteRecipe.recipe.recipeId")
    @Mapping(target = "image", source = "favoriteRecipe.recipe.image")
    @Mapping(target = "title", source = "favoriteRecipe.recipe.title")
    @Mapping(target = "description", source = "favoriteRecipe.recipe.description")
    @Mapping(target = "tags", source = "favoriteRecipe.recipe.tags")
    FavoriteRecipeDTO toFavoriteRecipeDTO(FavoriteRecipe favoriteRecipe);

    @Mapping(target = "recipeId", source = "recipe.recipeId")
    @Mapping(target = "image", source = "recipe.image")
    @Mapping(target = "title", source = "recipe.title")
    @Mapping(target = "description", source = "recipe.description")
    @Mapping(target = "tags", source = "recipe.tags")
    ShortRecipeResponseDTO toShortRecipeResponseDTO(Recipe recipe);

    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "instructions", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "image", ignore = true)
    Recipe toRecipe(RecipeRequestDTO recipeRequestDTO);

    @Mapping(target = "recipeId", source = "recipe.recipeId")
    @Mapping(target = "title", source = "recipe.title")
    @Mapping(target = "description", source = "recipe.description")
    @Mapping(target = "image", source = "recipe.image")
    @Mapping(target = "difficulty", source = "recipe.difficulty")
    @Mapping(target = "prepTime", source = "recipe.prepTime")
    @Mapping(target = "instructions", ignore = true)
    @Mapping(target = "portions", source = "recipe.portions")
    @Mapping(target = "ratings", source = "recipe.ratings")
    @Mapping(target = "tags", source = "recipe.tags")
    @Mapping(target = "average", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    RecipeResponseDTO toRecipeResponseDTO(Recipe recipe);

    @IterableMapping(elementTargetType = String.class)
    List<String> mapTagsToNames(List<Tag> tags);

    default String mapTagToName(Tag tag) {
        return tag.getName();
    }
}

