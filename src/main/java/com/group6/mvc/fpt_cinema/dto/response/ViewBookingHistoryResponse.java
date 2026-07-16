package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.RefundMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewBookingHistoryResponse {
    private Integer id;
    private String bookingCode;
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private PaymentMethod method;
    private String promotionCode;
    private String promotionName;
    private List<CreateBookingTicketResponse> tickets;
    private List<ProcessPaymentProductResponse> products;

    private Boolean refundRequested;
    private RefundMethod refundMethod;
    private Boolean refundCompleted;
    private LocalDateTime refundedAt;
    private String refundVoucherCode;

}
