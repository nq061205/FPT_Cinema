package com.group6.mvc.fpt_cinema.repository.projection;

import java.math.BigDecimal;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;

public interface PaymentMethodProjection {
    PaymentMethod getPaymentMethod();

    Integer getTransactionCount();

    BigDecimal getAmount();
}
