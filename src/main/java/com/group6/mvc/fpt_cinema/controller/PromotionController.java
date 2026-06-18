package com.group6.mvc.fpt_cinema.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.service.PromotionService;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    private PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

      @PostMapping("/detail")
        public ApiResponse<ViewPromotionResponse> viewPromotion(
                        @RequestBody SelectPromotionRequest request) {

                ApiResponse<ViewPromotionResponse> response = new ApiResponse<>();

                response.setMessage(
                                "Promotion details retrieved successfully!");

                response.setResult(
                                promotionService.viewPromotion(request));

                return response;
        }
}
