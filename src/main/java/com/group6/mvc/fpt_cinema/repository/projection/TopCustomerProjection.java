package com.group6.mvc.fpt_cinema.repository.projection;

import java.math.BigDecimal;

public interface TopCustomerProjection {
    String getCustomerName();

    Integer getBookingCount();

    BigDecimal getSpending();
}
