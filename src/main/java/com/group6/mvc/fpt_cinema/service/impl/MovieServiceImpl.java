package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Movies;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.service.MovieService;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl
        extends AbstractCrudService<Movies, Integer>
        implements MovieService {

    public MovieServiceImpl(MovieRepository repository) {
        super(repository);
    }
}
