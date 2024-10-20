package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gf.yummify.data.enums.ActivityType;
import com.gf.yummify.data.enums.RelatedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @NotNull
    private LocalDateTime activityDate;

    @NotNull
    private Long relatedEntityId;

    @Enumerated(EnumType.STRING)
    private RelatedEntity relatedEntity;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @NotNull
    private String description;

    @OneToOne(mappedBy = "activityLog", cascade = CascadeType.ALL)
    private Notification notification;

    public ActivityLog() {
        this.activityDate = LocalDateTime.now();
    }
}
