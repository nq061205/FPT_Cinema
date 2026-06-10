package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;
import org.springframework.stereotype.Service;

@Service
public class ShowtimeServiceImpl
        extends AbstractCrudService<Showtime, Integer>
        implements ShowtimeService {

    public ShowtimeServiceImpl(ShowtimeRepository repository) {
        super(repository);
    }
}
