package com.group6.mvc.fpt_cinema.dto.report.response.promotion;

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
public class PromotionReportResponse {
    private Integer totalPromotions;
    private Integer totalUsage;
    private BigDecimal totalDiscountAmount;
    private List<PromotionUsageItem> topPromotions;
}
