package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID tagId;

    @NotNull
    private String name;

    private @NotNull LocalDateTime creationDate;

    @ManyToMany(mappedBy = "tags")
    private List<Recipe> recipes;

    public Tag() {
        this.creationDate = LocalDateTime.now();
    }
}
