package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.IngredientStatus;
import com.gf.yummify.data.enums.IngredientType;
import com.gf.yummify.data.enums.UnitOfMeasure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Data
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID ingredientId;

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

    private @NotNull LocalDateTime creationDate;
    private LocalDateTime lastModification;

    @Transient
    private String formattedCreationDate;
    @Transient
    private String formattedLastModification;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public Ingredient() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
        updateFormattedDates();
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

    @PreUpdate
    public void setLastModification() {
        this.lastModification = LocalDateTime.now();
    }


}
