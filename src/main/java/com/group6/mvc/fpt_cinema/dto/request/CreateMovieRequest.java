package com.group6.mvc.fpt_cinema.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.group6.mvc.fpt_cinema.enums.MovieGenre;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieRequest {
    private String title;
    private MovieGenre genre;
    private Integer durationMinutes;
    private String ageRating;
    private String posterUrl;
    private String trailerUrl;
    private String description;
    private LocalDate releaseDate;
    private MovieStatus status;
    private LocalDateTime createdAt;
}
