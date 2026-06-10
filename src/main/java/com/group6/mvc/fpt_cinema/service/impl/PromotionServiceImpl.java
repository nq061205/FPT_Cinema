package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.repository.PromotionRepository;
import com.group6.mvc.fpt_cinema.service.PromotionService;
import org.springframework.stereotype.Service;

@Service
public class PromotionServiceImpl
        extends AbstractCrudService<Promotion, Integer>
        implements PromotionService {

    public PromotionServiceImpl(PromotionRepository repository) {
        super(repository);
    }
}
