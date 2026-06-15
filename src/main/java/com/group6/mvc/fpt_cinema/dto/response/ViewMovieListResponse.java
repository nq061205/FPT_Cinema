package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewMovieListResponse {
    private Integer id;
    private String title;
    private String genre;
    private Integer durationMinutes;
    private String ageRating;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String description;
}
