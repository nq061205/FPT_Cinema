package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentResponse {
    private String paymentCode;
    private String bookingCode;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    // Detailed Invoice/Receipt Fields
    private String movieTitle;
    private String roomName;
    private LocalDateTime startTime;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String promotionCode;
    private String promotionName;

    private List<CreateBookingTicketResponse> tickets;
    private List<ProcessPaymentProductResponse> products;
}
