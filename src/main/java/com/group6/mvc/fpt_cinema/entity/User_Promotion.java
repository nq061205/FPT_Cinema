package com.group6.mvc.fpt_cinema.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_promotions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "IX_user_promotion_unique",
            columnNames = { "user_id", "promotion_id" }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User_Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_user_promotions_users")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "promotion_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_user_promotions_promotions")
    )
    private Promotion promotion;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE";

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}