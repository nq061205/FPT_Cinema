package com.group6.mvc.fpt_cinema.dto.report.response.booking;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingReportResponse {
    private Integer totalBookings;
    private Integer completedBookings;
    private Integer cancelledBookings;
    private List<BookingTrendItem> bookingTrend;
    private List<BookingChannelItem> channelDistribution;
}
