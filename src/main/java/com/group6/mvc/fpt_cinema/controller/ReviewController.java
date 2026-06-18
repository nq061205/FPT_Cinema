package com.group6.mvc.fpt_cinema.controller;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.EditReviewRequest;
import com.group6.mvc.fpt_cinema.dto.request.ReviewRequest;
import com.group6.mvc.fpt_cinema.dto.response.ReviewResponse;
import com.group6.mvc.fpt_cinema.service.ReviewService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

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
    public ApiResponse<Page<ReviewResponse>> getReviewsByMovie(
    @PathVariable Integer movieId,
    @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
    return ApiResponse.<Page<ReviewResponse>>builder()
        .message("Reviews retrieved successfully")
        .result(reviewService.getReviewsByMovie(movieId, pageable))
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
