package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.Promotion;

public interface PromotionService extends CrudService<Promotion, Integer> {
    ViewPromotionResponse viewPromotion(SelectPromotionRequest request);
}
