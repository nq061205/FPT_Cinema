package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
