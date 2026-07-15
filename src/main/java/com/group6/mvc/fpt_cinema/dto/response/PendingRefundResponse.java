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
public class PendingRefundResponse {
    private Integer bookingId;
    private String bookingCode;
    private String movieTitle;
    private String customerName;
    private String customerPhone;
    private BigDecimal finalAmount;
    private RefundMethod refundMethod;
    private LocalDateTime refundRequestedAt;
}
