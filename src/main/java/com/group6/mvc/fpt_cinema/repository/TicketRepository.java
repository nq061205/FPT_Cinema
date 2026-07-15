package com.group6.mvc.fpt_cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    boolean existsByBookingIdAndStatus(Integer bookingId, String status);

    boolean existsBySeatIdAndShowtimeIdAndStatus(Integer seatId, Integer showtimeId, String status);

    List<Ticket> findByBookingId(Integer bookingId);
}
