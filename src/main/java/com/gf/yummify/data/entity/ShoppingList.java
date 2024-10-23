package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gf.yummify.data.enums.ListStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "shopping_lists")
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoppingListId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotNull
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ListStatus listStatus;
    private @NotNull LocalDateTime creationDate;
    private LocalDateTime lastModification;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ShoppingListItem> listItems;
    public ShoppingList() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }
}
