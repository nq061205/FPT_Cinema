package com.group6.mvc.fpt_cinema.repository.projection;

public interface PaymentStatusCountProjection {
    Integer getTotal();

    Integer getSuccessful();

    Integer getFailed();
}
