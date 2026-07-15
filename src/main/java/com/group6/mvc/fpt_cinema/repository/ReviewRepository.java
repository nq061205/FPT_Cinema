package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    int countByCustomerIdAndMovieId(Integer customerId, Integer movieId);
    boolean existsByCustomerIdAndBookingId(Integer customerId, Integer bookingId);
    Page<Review> findByMovieIdAndStatusOrderByCreatedAtDesc(Integer movieId, String Status, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE " + "(:movieId IS NULL OR r.movie.id = :movieId) AND " +
        "(:rating IS NULL OR r.rating = :rating)"
    ) 
    Page<Review> findByMovieIdAndRating(
        @Param("movieId") Integer movieId, 
        @Param("rating") Integer rating, 
        Pageable pageable
    ); 

    boolean existsByCustomerIdAndMovieId(Integer customerId, Integer movieId);
}
