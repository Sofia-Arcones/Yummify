package com.gf.yummify.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListRequestDTO {
    @NotNull
    private String title;
    private List<String> ingredients = new ArrayList<>();
    private List<Double> quantities = new ArrayList<>();
    private List<String> units = new ArrayList<>();
}
