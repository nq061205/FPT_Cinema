package com.group6.mvc.fpt_cinema.dto.payment.response;

import java.math.BigDecimal;

import com.group6.mvc.fpt_cinema.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VnpayReturnResponse {

    private String paymentCode;
    private String bookingCode;
    private PaymentStatus status;
    private BigDecimal amount;
    private String responseCode;
    private String message;
}
