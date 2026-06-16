package com.group6.mvc.fpt_cinema.integration.n8n;

import java.time.LocalDate;

public record N8nMovieContext(
        Integer id,
        String title,
        String genre,
        Integer durationMinutes,
        String ageRating,
        LocalDate releaseDate,
        String description,
        String status) {
}
