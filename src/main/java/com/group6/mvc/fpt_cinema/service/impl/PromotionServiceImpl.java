package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Promotions;
import com.group6.mvc.fpt_cinema.repository.PromotionRepository;
import com.group6.mvc.fpt_cinema.service.PromotionService;
import org.springframework.stereotype.Service;

@Service
public class PromotionServiceImpl
        extends AbstractCrudService<Promotions, Integer>
        implements PromotionService {

    public PromotionServiceImpl(PromotionRepository repository) {
        super(repository);
    }
}
