package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID challengeId;

    @NotNull
    private String title;

    @NotNull
    private String description;
    @NotNull
    private Integer winnerQuantity;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @NotNull
    private String reward;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<ChallengeParticipation> participations;

    private @NotNull LocalDateTime creationDate;
    private LocalDateTime lastModification;

    public Challenge() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }
}
