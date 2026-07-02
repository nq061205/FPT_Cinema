package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.group6.mvc.fpt_cinema.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    private String bookingCode;
    private String movieTitle;
    private String roomName;
    private LocalDateTime startTime;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private LocalDateTime expiresAt;
    private List<CreateBookingTicketResponse> tickets;
}
