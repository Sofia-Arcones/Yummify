package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.ListStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "shopping_lists")
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoppingListId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ListStatus listStatus;
    @NotNull
    private LocalDate creationDate;
    private LocalDate lastModification;
    public ShoppingList() {
        this.creationDate = LocalDate.now();
        this.lastModification = LocalDate.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDate.now();
    }
}
