package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

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
        response.setReleaseDate(movie.getReleaseDate());
        response.setPosterUrl(movie.getPosterUrl());
        response.setTrailerUrl(movie.getTrailerUrl());
        response.setDescription(movie.getDescription());
        return response;
    }

}

