package com.group6.mvc.fpt_cinema.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group6.mvc.fpt_cinema.dto.request.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.ReviewResponse;
import com.group6.mvc.fpt_cinema.entity.Review;

public interface ReviewService extends CrudService<Review, Integer> {
    ReviewResponse createReview(ReviewRequest request, Integer userId);

    Page<ReviewResponse> getReviewsByMovie(Integer movieId, Pageable pageable);

    ReviewResponse editReview(Integer reviewId, EditReviewRequest request, Integer userId);

    Page<ReviewResponse> getAllReviews(Integer movieId, Integer rating, Pageable pageable);
    void deleteReview(Integer reviewId);
}
