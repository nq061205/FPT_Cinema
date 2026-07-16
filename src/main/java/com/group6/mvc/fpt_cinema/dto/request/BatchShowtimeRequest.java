package com.group6.mvc.fpt_cinema.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class BatchShowtimeRequest {

    private Integer movieId;
    private List<Integer> roomIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<LocalTime> dailyStartTimes;
    private BigDecimal basePrice;
    private Integer cleaningBufferMinutes;

}
