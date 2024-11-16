package com.gf.yummify.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListItemRequestDTO {
    private UUID shoppingListId;
    private String ingredientName;
    private Double quantity;
    private String unitOfMeasure;
}
