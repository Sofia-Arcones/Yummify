package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID rateId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    @NotNull
    private Double rating;
    @OneToMany(mappedBy = "rate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;
    private @NotNull LocalDateTime creationDate;
    private LocalDateTime lastModification;
    public Rating() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }
}
