package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Seats;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.service.SeatService;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl
        extends AbstractCrudService<Seats, Integer>
        implements SeatService {

    public SeatServiceImpl(SeatRepository repository) {
        super(repository);
    }
}
