package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByIdAndCustomerId(Integer id, Integer customerId);

    Optional<Booking> findByBookingCode(String bookingCode);

    Optional<Booking> findByBookingCodeAndCustomerId(String bookingCode, Integer customerId);

    Page<Booking> findByCustomerId(Integer customerId, Pageable pageable);

    long countByShowtimeIdAndStatus(Integer showtimeId, BookingStatus status); 

    
}
