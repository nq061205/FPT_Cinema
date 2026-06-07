package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Reviews;
import com.group6.mvc.fpt_cinema.repository.ReviewRepository;
import com.group6.mvc.fpt_cinema.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl
        extends AbstractCrudService<Reviews, Integer>
        implements ReviewService {

    public ReviewServiceImpl(ReviewRepository repository) {
        super(repository);
    }
}
