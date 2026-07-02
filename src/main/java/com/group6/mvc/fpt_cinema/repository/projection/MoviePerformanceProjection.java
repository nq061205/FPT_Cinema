package com.group6.mvc.fpt_cinema.repository.projection;

import java.math.BigDecimal;

/** Per-movie performance row returned by the database. */
public interface MoviePerformanceProjection {
    Integer getMovieId();
    String getMovieTitle();
    Integer getTicketsSold();
    Integer getBookingCount();
    BigDecimal getRevenue();
}
