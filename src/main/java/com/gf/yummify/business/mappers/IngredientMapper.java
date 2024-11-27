package com.gf.yummify.business.mappers;

import com.gf.yummify.data.entity.Ingredient;
import com.gf.yummify.presentation.dto.IngredientRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    Ingredient toIngredient(IngredientRequestDTO ingredientRequestDTO);
}
