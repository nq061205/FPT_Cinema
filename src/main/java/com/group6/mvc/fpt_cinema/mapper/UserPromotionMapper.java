package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;

@Component
public class UserPromotionMapper {

    public ViewUserPromotionResponse toResponse(
            User_Promotion userPromotion) {

        ViewPromotionResponse promotion = new ViewPromotionResponse();
        promotion.setId(userPromotion.getPromotion().getId());
        promotion.setPromotionCode(userPromotion.getPromotion().getPromotionCode());
        promotion.setName(userPromotion.getPromotion().getName());
        promotion.setPromotionType(userPromotion.getPromotion().getPromotionType());
        promotion.setDiscountValue(userPromotion.getPromotion().getDiscountValue());
        promotion.setStartDate(userPromotion.getPromotion().getStartDate());
        promotion.setEndDate(userPromotion.getPromotion().getEndDate());
        promotion.setIsActive(userPromotion.getPromotion().getIsActive());

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