package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientAutocompleteDTO {
    private String name;
    private String predeterminedUnit;

}
