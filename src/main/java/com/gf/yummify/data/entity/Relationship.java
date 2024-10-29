package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gf.yummify.data.enums.RelationshipStatus;
import com.gf.yummify.data.enums.RelationshipType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "relationships")
public class Relationship {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID relationshipId;

    @ManyToOne
    @JoinColumn(name = "requesting_user", nullable = false)
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "requested_user", nullable = false)
    @JsonBackReference
    private User receiver;


    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipStatus relationshipStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipType relationshipType;

    private @NotNull LocalDateTime creationDate;
    private LocalDateTime lastModification;

    public Relationship() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }

}
