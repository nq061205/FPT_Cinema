package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.group6.mvc.fpt_cinema.enums.RefundMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketLookupResponse {
    private String ticketCode;
    private String movieTitle;
    private String roomName;
    private String seatRow;
    private Integer seatNumber;
    private LocalDateTime startTime;
    private String status;
    private LocalDateTime checkedInAt;

    private Integer bookingId;
    private String bookingCode;
    private BigDecimal finalAmount;

    private Boolean refundRequested;
    private RefundMethod refundMethod;
    private Boolean refundCompleted;
    private LocalDateTime refundedAt;
}
