package com.gf.yummify.presentation.dto;

import com.gf.yummify.data.enums.UnitOfMeasure;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecipeIngredientDTO {
    private String ingredientName;
    private Double quantity;
    private UnitOfMeasure unitOfMeasure;
}
