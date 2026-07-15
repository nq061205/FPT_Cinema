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
public class ConfirmRefundResponse {
    private String bookingCode;
    private RefundMethod refundMethod;
    private BigDecimal refundAmount;
    private LocalDateTime refundedAt;
    private String voucherCode;
}
