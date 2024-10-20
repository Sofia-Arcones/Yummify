package com.gf.yummify.data.entity;

import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "relationships")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationshipId;

    @ManyToOne
    @JoinColumn(name = "requesting_user", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "requested_user", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipStatus relationshipStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipType relationshipType;

    @NotNull
    private LocalDate creationDate;
    private LocalDate lastModification;

    public Relationship() {
        this.creationDate = LocalDate.now();
        this.lastModification = LocalDate.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDate.now();
    }

}
