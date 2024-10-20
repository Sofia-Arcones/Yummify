package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gf.yummify.data.enums.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Size(max = 200)
    @NotNull
    private String title;

    private String description;

    private String image;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Difficulty difficulty;

    @NotNull
    @Positive
    private Integer prepTime;

    @Lob
    @NotNull
    private String instructions;

    @NotNull
    private LocalDate creationDate;
    private LocalDate lastModification;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.creationDate = LocalDate.now();
        this.lastModification = LocalDate.now();
    }

    @PreUpdate
    public void setLastModification() {
        this.lastModification = LocalDate.now();
    }
}
