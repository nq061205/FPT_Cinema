package com.group6.mvc.fpt_cinema.dto.report.response.booking;

import com.group6.mvc.fpt_cinema.enums.BookingChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingChannelItem {
    private BookingChannel channel;
    private Integer bookingCount;
}
