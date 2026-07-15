package com.group6.mvc.fpt_cinema.repository.projection;

public interface BookingTrendProjection {
    String getPeriod();

    Integer getBookingCount();

    Integer getCompletedCount();

    Integer getCancelledCount();
}
