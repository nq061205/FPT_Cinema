package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.repository.PromotionRepository;
import com.group6.mvc.fpt_cinema.service.PromotionService;
import org.springframework.stereotype.Service;

@Service
public class PromotionServiceImpl
        extends AbstractCrudService<Promotion, Integer>
        implements PromotionService {

            private PromotionRepository promotionRepository;
    public PromotionServiceImpl(PromotionRepository repository, PromotionRepository promotionRepository) {
        super(repository);
        this.promotionRepository = promotionRepository;
    }

    @Override
    public ViewPromotionResponse viewPromotion(SelectPromotionRequest request) {
    
        Promotion promotion = promotionRepository.findByPromotionCode(request.getPromotionId());
       

        ViewPromotionResponse response = new ViewPromotionResponse();
        response.setName(promotion.getName());
        response.setPromotionType(promotion.getPromotionType());
        response.setDiscountValue(promotion.getDiscountValue());
        response.setIsActive(promotion.getIsActive());

        return response;
    }
}
