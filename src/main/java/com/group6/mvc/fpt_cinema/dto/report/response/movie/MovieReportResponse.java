package com.group6.mvc.fpt_cinema.dto.report.response.movie;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReportResponse {
    private Integer totalMovies;
    private Integer totalTicketsSold;
    private Double averageOccupancyRate;
    private List<MoviePerformanceItem> topMovies;
}
