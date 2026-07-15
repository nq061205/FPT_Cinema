package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByIdAndCustomerId(Integer id, Integer customerId);

    Page<Booking> findByCustomerId(Integer customerId, Pageable pageable);

    long countByShowtimeIdAndStatus(Integer showtimeId, BookingStatus status); 



@Query("""
    SELECT b FROM Booking b
    JOIN Ticket t ON t.booking.id = b.id
    WHERE b.customer.id = :userId
      AND b.showtime.movie.id = :movieId
      AND b.status = 'CONFIRMED'
      AND t.status = 'USED'
      AND b.showtime.startTime < :now
    ORDER BY b.showtime.startTime DESC
""")
List<Booking> findCompletedBooking(@Param("userId") Integer userId, @Param("movieId") Integer movieId, @Param("now") LocalDateTime now, Pageable pageable);
}
