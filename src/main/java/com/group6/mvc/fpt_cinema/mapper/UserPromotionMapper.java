package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;

@Component
public class UserPromotionMapper {

    public ViewUserPromotionResponse toResponse(
            User_Promotion userPromotion) {

        ViewPromotionResponse promotion =
                new ViewPromotionResponse(
                        userPromotion.getPromotion().getName(),
                        userPromotion.getPromotion().getPromotionType(),
                        userPromotion.getPromotion().getDiscountValue(),
                        userPromotion.getPromotion().getIsActive());

        ViewUserPromotionResponse response =
                new ViewUserPromotionResponse();

        response.setUserPromotionId(
                userPromotion.getId());

        response.setStatus(
                userPromotion.getStatus().name());

        response.setAssignedAt(
                userPromotion.getAssignedAt());

        response.setPromotion(
                promotion);

        return response;
    }
}