package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.entity.Promotion;

@Component
public class PromotionMapper {
    public SelectPromotionRequest toSelectPromotionRequest(Promotion promotion) {
        SelectPromotionRequest request = new SelectPromotionRequest();
        request.setPromotionId(promotion.getId());
        return request;
    }
}
