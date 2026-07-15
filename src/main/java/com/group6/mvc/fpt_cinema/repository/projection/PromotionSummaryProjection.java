package com.group6.mvc.fpt_cinema.repository.projection;

import java.math.BigDecimal;

public interface PromotionSummaryProjection {
    Integer getTotalPromotions();

    Integer getTotalUsage();

    BigDecimal getTotalDiscountAmount();
}
