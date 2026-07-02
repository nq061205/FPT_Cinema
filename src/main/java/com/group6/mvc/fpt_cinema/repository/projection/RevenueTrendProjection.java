package com.group6.mvc.fpt_cinema.repository.projection;

import java.math.BigDecimal;

public interface RevenueTrendProjection {
    String getPeriod();

    BigDecimal getRevenue();

    Integer getOrderCount();
}
