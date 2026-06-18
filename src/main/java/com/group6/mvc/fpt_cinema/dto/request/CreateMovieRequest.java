package com.group6.mvc.fpt_cinema.dto.request;

import java.time.LocalDateTime;

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
    private String genre;
    private Integer durationMinutes;
    private String ageRating;
    private String posterUrl;
    private String trailerUrl;
    private String description;
    private LocalDateTime createdAt;
}
