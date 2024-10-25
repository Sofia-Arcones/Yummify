package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @NotNull
    private String name;

    private @NotNull LocalDateTime creationDate;

    @ManyToMany(mappedBy = "tags")
    private List<Recipe> recipes;

    public Tag() {
        this.creationDate = LocalDateTime.now();
    }
}
