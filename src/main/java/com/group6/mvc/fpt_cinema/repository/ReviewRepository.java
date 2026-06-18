package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    int countByCustomerIdAndMovieId(Integer customerId, Integer movieId);
    boolean existsByCustomerIdAndBookingId(Integer customerId, Integer bookingId);
    Page<Review> findByMovieIdAndStatusOrderByCreatedAtDesc(Integer movieId, String Status, Pageable pageable);
}
