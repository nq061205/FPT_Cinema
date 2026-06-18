package com.group6.mvc.fpt_cinema.dto.report.response.revenue;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRevenueItem {
    private String movieTitle;
    private BigDecimal revenue;
}
