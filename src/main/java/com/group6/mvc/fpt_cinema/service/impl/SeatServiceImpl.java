package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.service.SeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl
        extends AbstractCrudService<Seat, Integer>
        implements SeatService {

    public SeatServiceImpl(SeatRepository repository) {
        super(repository);
    }
}
