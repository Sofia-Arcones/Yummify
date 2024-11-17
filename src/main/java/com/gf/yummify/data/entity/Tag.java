package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Transient
    private String formattedCreationDate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Tag() {
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
