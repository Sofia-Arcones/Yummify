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
    @JoinColumn(name = "requestingUser", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "requestedUser", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipStatus relationshipStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelationshipType relationshipType;

    @NotNull
    private LocalDate creationDate = LocalDate.now();
    private LocalDate lastModification = LocalDate.now();

}
