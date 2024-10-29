package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
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


    @NotNull
    private Boolean isDeleted = false;

    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
        this.lastModification = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModification = LocalDateTime.now();
    }
}
