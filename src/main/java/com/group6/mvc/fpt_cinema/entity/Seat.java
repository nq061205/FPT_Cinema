package com.group6.mvc.fpt_cinema.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(name = "IX_seats_room_row_number", columnNames = { "room_id", "seat_row", "seat_number" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "seat_row", nullable = false, length = 10)
    private String seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_type", nullable = false, length = 20)
    private String seatType = "NORMAL";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

}