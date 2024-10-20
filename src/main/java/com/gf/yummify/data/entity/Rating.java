package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @NotNull
    private Double rating;
    
    @NotNull
    private LocalDate creationDate;
    private LocalDate lastModification;
    public Rating() {
        this.creationDate = LocalDate.now();
        this.lastModification = LocalDate.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDate.now();
    }
}
