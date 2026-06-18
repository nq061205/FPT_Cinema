package com.group6.mvc.fpt_cinema.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionListResponse;
import com.group6.mvc.fpt_cinema.service.UserPromotionService;

@RestController
@RequestMapping("/api/user-promotion")
public class UserPromotionController {

    private final UserPromotionService userPromotionService;

    public UserPromotionController(
            UserPromotionService userPromotionService) {

        this.userPromotionService = userPromotionService;
    }

    @PostMapping("/my-promotions")
    public ApiResponse<ViewUserPromotionListResponse>
            viewMyPromotions(
                    @AuthenticationPrincipal Jwt jwt) {

        Integer userId =
                Integer.valueOf(
                        jwt.getClaim("userId").toString());

        Pageable pageable =
                PageRequest.of(0, 20);

        ApiResponse<ViewUserPromotionListResponse>
                response = new ApiResponse<>();

        response.setMessage(
                "Promotions retrieved successfully!");

        response.setResult(
                userPromotionService
                        .viewUserPromotions(
                                userId,
                                pageable));

        return response;
    }
}