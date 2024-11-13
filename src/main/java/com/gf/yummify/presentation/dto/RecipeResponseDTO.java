package com.gf.yummify.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDTO {
    @NotBlank(message = "El título es obligatorio.")
    private String title;
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;
    @NotNull(message = "La imagen es obligatoria.")
    private String image;
    @NotNull(message = "La dificultad es obligatoria.")
    private String difficulty;
    @NotNull(message = "El tiempo de preparación es obligatorio.")
    private Integer prepTime;
    @NotNull(message = "La cantidad de comensales es obligatoria.")
    private Integer portions;
    @NotNull(message = "Los ingredientes son obligatorios.")
    private List<String> ingredients;
    @NotNull(message = "Las cantidades son obligatorias.")
    private List<Double> quantities;
    @NotNull(message = "Las unidades son obligatorias.")
    private List<String> units;
    @NotNull(message = "Las instrucciones son obligatorias.")
    private List<String> instructions;
    private List<String> tags;
}
