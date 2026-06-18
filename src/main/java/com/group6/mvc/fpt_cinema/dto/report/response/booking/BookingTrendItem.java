package com.group6.mvc.fpt_cinema.dto.report.response.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingTrendItem {
    private String period;
    private Integer bookingCount;
    private Integer completedCount;
    private Integer cancelledCount;
}
