package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.FavoriteRecipe;
import com.gf.yummify.data.entity.Tag;
import com.gf.yummify.presentation.dto.FavoriteRecipeDTO;
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

    @IterableMapping(elementTargetType = String.class)
    List<String> mapTagsToNames(List<Tag> tags);
    default String mapTagToName(Tag tag) {
        return tag.getName();
    }
}

