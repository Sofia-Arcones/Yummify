package com.gf.yummify.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @OneToOne
    @JoinColumn(name = "rate_id", nullable = false, unique = true)  // Relación uno a uno con Rate
    private Rating rate;

    @NotNull
    private String text;  // Texto del comentario

}

