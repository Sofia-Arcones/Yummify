package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID activityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotNull
    private LocalDateTime activityDate;

    @NotNull
    private Long relatedEntityId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RelatedEntity relatedEntity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ActivityType activityType;

    @NotNull
    private String description;

    @OneToOne(mappedBy = "activityLog", cascade = CascadeType.ALL)
    private Notification notification;
    @Transient
    private String formattedActivityDate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    public ActivityLog() {
        this.activityDate = LocalDateTime.now();
        updateFormattedDates();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateFormattedDates() {
        if (activityDate != null) {
            this.formattedActivityDate = activityDate.format(FORMATTER);
        }
    }
}
