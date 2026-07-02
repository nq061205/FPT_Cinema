package com.group6.mvc.fpt_cinema.integration.n8n;

import java.time.LocalDate;

import com.group6.mvc.fpt_cinema.enums.MovieGenre;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;

public record N8nMovieContext(
                Integer id,
                String title,
                MovieGenre genre,
                Integer durationMinutes,
                String ageRating,
                LocalDate releaseDate,
                String description,
                MovieStatus status) {
}
