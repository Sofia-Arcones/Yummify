package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID recipeIngredientId;
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UnitOfMeasure unitOfMeasure;
    @Positive
    @NotNull
    private Double quantity;

    private @NotNull LocalDateTime creationDate;
    @Transient
    private String formattedCreationDate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public RecipeIngredient() {
        this.creationDate = LocalDateTime.now();
        updateFormattedDates();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateFormattedDates() {
        if (creationDate != null) {
            this.formattedCreationDate = creationDate.format(FORMATTER);
        }
    }
}
