package com.group6.mvc.fpt_cinema.dto.report.response.movie;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoviePerformanceItem {
    private String movieTitle;
    private Integer ticketsSold;
    private Integer bookingCount;
    private BigDecimal revenue;
    private Double occupancyRate;
}
