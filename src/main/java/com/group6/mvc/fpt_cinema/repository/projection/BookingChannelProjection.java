package com.group6.mvc.fpt_cinema.repository.projection;

import com.group6.mvc.fpt_cinema.enums.BookingChannel;

public interface BookingChannelProjection {
    BookingChannel getChannel();

    Integer getBookingCount();
}
