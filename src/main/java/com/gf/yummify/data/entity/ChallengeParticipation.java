package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Entity
@Table(name = "challenge_participations")
public class ChallengeParticipation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID challengeParticipationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;
    @NotNull
    private Boolean isWinner;
    @NotNull
    private LocalDateTime participationDate;
    @Transient
    private String formattedParticipationDate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ChallengeParticipation() {
        this.participationDate = LocalDateTime.now();
        updateFormattedDates();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateFormattedDates() {
        if (participationDate != null) {
            this.formattedParticipationDate = participationDate.format(FORMATTER);
        }
    }

}
