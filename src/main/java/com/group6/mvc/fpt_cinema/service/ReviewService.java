package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.review.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.review.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.review.ReviewResponse;
import com.group6.mvc.fpt_cinema.entity.Review;

import java.util.List;

public interface ReviewService extends CrudService<Review, Integer> {
    ReviewResponse createReview (ReviewRequest request, Integer userId);

    List<ReviewResponse> getReviewsByMovie(Integer movieId);
    ReviewResponse editReview(Integer reviewId, EditReviewRequest request, Integer userId); 
}
