package com.group6.mvc.fpt_cinema.dto.report.response.revenue;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private List<RevenueTrendItem> revenueTrend;
    private List<MovieRevenueItem> movieRevenue;
}
