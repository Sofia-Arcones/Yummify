package com.gf.yummify.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "senderUser", nullable = false)
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiverUser", nullable = false)
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
