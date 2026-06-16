package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    int countByCustomerIdAndMovieId(Integer customerId, Integer movieId);
    boolean existsByCustomerIdAndBookingId(Integer customerId, Integer bookingId);
    List<Review> findByMovieIdAndStatusOrderByCreatedAtDesc(Integer movieId, String Status) ;
}
