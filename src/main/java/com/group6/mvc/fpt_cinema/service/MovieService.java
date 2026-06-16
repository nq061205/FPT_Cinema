package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.entity.Movie;

public interface MovieService extends CrudService<Movie, Integer> {
    List<ViewMovieListResponse> getAllMovies();

    List<ViewMovieListResponse> viewMovieList(ViewMovieListRequest request);
}
