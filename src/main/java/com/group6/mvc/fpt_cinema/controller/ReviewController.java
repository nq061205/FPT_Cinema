package com.group6.mvc.fpt_cinema.controller;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.review.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.review.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.review.ReviewResponse;
import com.group6.mvc.fpt_cinema.service.ReviewService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ApiResponse<ReviewResponse> createReview (@RequestBody ReviewRequest request, 
        @AuthenticationPrincipal Jwt jwt
    ){

        Integer userId = Integer.valueOf(jwt.getClaimAsString("userId"));
        ReviewResponse response = reviewService.createReview(request, userId);
        return ApiResponse.<ReviewResponse>builder()
                .message("Review submitted successfully")
                .result(response)
                .build();
    }

    @GetMapping("/movie/{movieId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByMovie(@PathVariable Integer movieId){
        return ApiResponse.<List<ReviewResponse>>builder()
                .message("Review retrieved successfully")
                .result(reviewService.getReviewsByMovie(movieId))
                .build();
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> editReview(
        @PathVariable Integer reviewId, 
        @RequestBody EditReviewRequest request, 
        @AuthenticationPrincipal Jwt jwt
    ){
        Integer userId = Integer.valueOf(jwt.getClaimAsString("userId")); 
        ReviewResponse response = reviewService.editReview(reviewId, request, userId); 
        return ApiResponse.<ReviewResponse>builder()
        .message("Review updated successfully")
        .result(response)
        .build(); 

    }
}
