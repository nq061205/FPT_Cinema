package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ApplyPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;

@Component
public class PromotionMapper {

    public SelectPromotionRequest toSelectPromotionRequest(Promotion promotion) {
        SelectPromotionRequest request = new SelectPromotionRequest();
        request.setPromotionId(promotion.getId());
        return request;
    }

    public ApplyPromotionResponse toApplyPromotionResponse(User_Promotion userPromotion) {
        Promotion promotion = userPromotion.getPromotion();
        return ApplyPromotionResponse.builder()
                .name(promotion.getName())
                .promotionType(promotion.getPromotionType())
                .discountValue(promotion.getDiscountValue())
                .build();
    }
}
