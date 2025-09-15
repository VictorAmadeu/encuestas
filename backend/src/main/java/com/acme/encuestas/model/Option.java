package com.acme.encuestas.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "options")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "order_idx", nullable = false)
    private int orderIdx;
}
