package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ApplyPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ApplyPromotionResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.service.PromotionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/detail")
    public ApiResponse<ViewPromotionResponse> viewPromotion(
            @RequestBody SelectPromotionRequest request) {

        ApiResponse<ViewPromotionResponse> response = new ApiResponse<>();
        response.setMessage("Promotion details retrieved successfully!");
        response.setResult(promotionService.viewPromotion(request));
        return response;
    }

    @PostMapping("/apply")
    public ApiResponse<ApplyPromotionResponse> applyPromotion(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ApplyPromotionRequest request) {

        Integer userId = Integer.valueOf(jwt.getClaim("userId").toString());

        return ApiResponse.<ApplyPromotionResponse>builder()
                .message("Promotion applied successfully!")
                .result(promotionService.applyPromotion(userId, request))
                .build();
    }
}
