package com.protontype.chessapp.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private String color; // "WHITE" or "BLACK"

    @Column(columnDefinition = "TEXT")
    private String moves;

    private String result; // WIN, LOSS, DRAW
    private Integer eloChange = 0;
}
