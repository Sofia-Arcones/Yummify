package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shopping_list_items")
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoppingListItemId;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Positive
    private Double quantity;

    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @NotNull
    private Boolean isPurchased;

    private @NotNull LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModification = LocalDateTime.now();

    public ShoppingListItem() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }

}
