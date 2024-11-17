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
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID messageId;

    @ManyToOne
    @JoinColumn(name = "sender_user", nullable = false) // Nombres de columnas consistentes
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_user", nullable = false) // Consistente con la entidad User
    @JsonBackReference
    private User receiver;

    @NotNull
    private String content;

    @NotNull
    private Boolean isRead = false;

    @NotNull
    private LocalDateTime sentAt;
    private LocalDateTime lastModification;
    @Transient
    private String formattedSentAt;
    @Transient
    private String formattedLastModification;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


    @NotNull
    private Boolean isDeleted = false;

    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
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
        if (sentAt != null) {
            this.formattedSentAt = sentAt.format(FORMATTER);
        }
        if (lastModification != null) {
            this.formattedLastModification = lastModification.format(FORMATTER);
        }
    }
}
