package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.CreateMovieRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateMovieResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.entity.Movie;

@Component
public class MovieMapper {


    public ViewMovieListResponse toMovieResponse(Movie movie) {
        ViewMovieListResponse response = new ViewMovieListResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setGenre(movie.getGenre());
        response.setDurationMinutes(movie.getDurationMinutes());
        response.setAgeRating(movie.getAgeRating());
        response.setStatus(movie.getStatus());
        response.setReleaseDate(movie.getReleaseDate());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setDescription(movie.getDescription());
        return response;
    }

    public Movie toMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setAgeRating(request.getAgeRating());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setDescription(request.getDescription());
        return movie;
    }

    public CreateMovieResponse toCreateMovieResponse(Movie movie) {
        CreateMovieResponse response = new CreateMovieResponse();
        response.setGenre(movie.getGenre());
        response.setTitle(movie.getTitle());
        return response;
    }
}

