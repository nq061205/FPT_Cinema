package com.group6.mvc.fpt_cinema.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.ApplyPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.request.SelectPromotionRequest;
import com.group6.mvc.fpt_cinema.dto.response.ApplyPromotionResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.UserPromotionStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.PromotionMapper;
import com.group6.mvc.fpt_cinema.repository.PromotionRepository;
import com.group6.mvc.fpt_cinema.repository.UserPromotionRepository;
import com.group6.mvc.fpt_cinema.service.PromotionService;

@Service
public class PromotionServiceImpl
        extends AbstractCrudService<Promotion, Integer>
        implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final UserPromotionRepository userPromotionRepository;
    private final PromotionMapper promotionMapper;

    public PromotionServiceImpl(PromotionRepository promotionRepository,
            UserPromotionRepository userPromotionRepository,
            PromotionMapper promotionMapper) {
        super(promotionRepository);
        this.promotionRepository = promotionRepository;
        this.userPromotionRepository = userPromotionRepository;
        this.promotionMapper = promotionMapper;
    }

    @Override
    public ViewPromotionResponse viewPromotion(SelectPromotionRequest request) {
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        ViewPromotionResponse response = new ViewPromotionResponse();
        response.setName(promotion.getName());
        response.setPromotionType(promotion.getPromotionType());
        response.setDiscountValue(promotion.getDiscountValue());
        response.setIsActive(promotion.getIsActive());
        return response;
    }

    @Override
    @Transactional
    public ApplyPromotionResponse applyPromotion(Integer userId, ApplyPromotionRequest request) {
        User_Promotion userPromotion = userPromotionRepository
                .findByUserIdAndPromotionId(userId, request.getPromotionId())
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        if (UserPromotionStatus.USED == userPromotion.getStatus()) {
            throw new AppException(ErrorCode.PROMOTION_ALREADY_USED);
        }

        Promotion promotion = userPromotion.getPromotion();
        if (!promotion.getIsActive()) {
            throw new AppException(ErrorCode.PROMOTION_INACTIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate())) {
            throw new AppException(ErrorCode.PROMOTION_NOT_STARTED);
        }
        if (now.isAfter(promotion.getEndDate())) {
            throw new AppException(ErrorCode.PROMOTION_EXPIRED);
        }

        userPromotion.setStatus(UserPromotionStatus.USED);
        userPromotion.setUsedAt(now);
        userPromotionRepository.save(userPromotion);

        return promotionMapper.toApplyPromotionResponse(userPromotion);
    }
}
