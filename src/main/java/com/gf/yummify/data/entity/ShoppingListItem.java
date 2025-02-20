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
@Table(name = "shopping_list_items")
public class ShoppingListItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID shoppingListItemId;

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
    @Transient
    private String formattedCreationDate;
    @Transient
    private String formattedLastModification;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ShoppingListItem() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
        updateFormattedDates();
    }

    @Override
    public String toString() {
        return "ShoppingListItem{" +
                "shoppingListItemId=" + shoppingListItemId +
                ", ingredient=" + ingredient +
                ", quantity=" + quantity +
                ", unitOfMeasure=" + unitOfMeasure +
                ", isPurchased=" + isPurchased +
                ", creationDate=" + creationDate +
                ", lastModification=" + lastModification +
                '}';
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateFormattedDates() {
        if (creationDate != null) {
            this.formattedCreationDate = creationDate.format(FORMATTER);
        }
        if (lastModification != null) {
            this.formattedLastModification = lastModification.format(FORMATTER);
        }
    }

}
