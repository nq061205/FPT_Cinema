package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.ReviewResponse;
import com.group6.mvc.fpt_cinema.entity.Review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService extends CrudService<Review, Integer> {
    ReviewResponse createReview (ReviewRequest request, Integer userId);

    Page<ReviewResponse> getReviewsByMovie(Integer movieId, Pageable pageable);
    ReviewResponse editReview(Integer reviewId, EditReviewRequest request, Integer userId);
}
