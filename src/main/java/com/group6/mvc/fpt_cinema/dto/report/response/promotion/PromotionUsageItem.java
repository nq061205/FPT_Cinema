package com.group6.mvc.fpt_cinema.dto.report.response.promotion;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUsageItem {
    private String promotionCode;
    private BigDecimal discountAmount;
}
