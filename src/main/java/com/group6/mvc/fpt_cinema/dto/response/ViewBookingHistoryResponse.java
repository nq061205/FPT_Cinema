package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.group6.mvc.fpt_cinema.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewBookingHistoryResponse {
    private String bookingCode;
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;

}
