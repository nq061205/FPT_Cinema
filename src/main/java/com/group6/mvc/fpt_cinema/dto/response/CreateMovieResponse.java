package com.group6.mvc.fpt_cinema.dto.response;

import com.group6.mvc.fpt_cinema.enums.MovieGenre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieResponse {
    private String title;
    private MovieGenre genre;
}
