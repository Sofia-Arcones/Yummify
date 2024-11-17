package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Transient
    private String formattedCreationDate;
    @Transient
    private String formattedLastModification;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Challenge() {
        this.creationDate = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
        updateFormattedDates();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateFormattedDates() {
        if (creationDate != null) {
            this.formattedCreationDate = creationDate.format(FORMATTER);
        }
        if (lastModification != null) {
            this.formattedLastModification = lastModification.format(FORMATTER);
        }
    }
}
