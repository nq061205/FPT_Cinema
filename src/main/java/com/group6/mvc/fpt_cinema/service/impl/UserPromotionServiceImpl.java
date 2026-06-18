package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.dto.response.ViewPromotionResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionListResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionResponse;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;
import com.group6.mvc.fpt_cinema.mapper.UserPromotionMapper;
import com.group6.mvc.fpt_cinema.repository.UserPromotionRepository;
import com.group6.mvc.fpt_cinema.service.UserPromotionService;

@Service
public class UserPromotionServiceImpl
        extends AbstractCrudService<User_Promotion, Integer>
        implements UserPromotionService {
    private UserPromotionRepository userPromotionRepository;

    private UserPromotionMapper userPromotionMapper;

    public UserPromotionServiceImpl(UserPromotionRepository repository,
            UserPromotionRepository userPromotionRepository,
            UserPromotionMapper userPromotionMapper) {
        super(repository);

        this.userPromotionRepository = userPromotionRepository;
        this.userPromotionMapper = userPromotionMapper;
    }

    @Override
    public ViewUserPromotionListResponse viewUserPromotions(
            Integer userId,
            Pageable pageable) {

        Page<User_Promotion> page = userPromotionRepository.findByUserId(
                userId,
                pageable);

        List<ViewUserPromotionResponse> promotions = page.getContent()
                .stream()
                .map(userPromotionMapper::toResponse)
                .toList();

        return new ViewUserPromotionListResponse(
                promotions);
    }
}
