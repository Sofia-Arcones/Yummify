package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.enums.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @NotNull
    @NotBlank
    @Size(max = 50)
    private String ingredientName;

    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @Enumerated(EnumType.STRING)
    private IngredientType ingredientType;

    @Positive
    private Double calories;

    @Enumerated(EnumType.STRING)
    @NotNull
    private IngredientStatus ingredientStatus;

    @NotNull
    private LocalDate creationDate = LocalDate.now();
    private LocalDate lastModification = LocalDate.now();


}
