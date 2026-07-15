package com.group6.mvc.fpt_cinema.dto.payment.response;

import java.math.BigDecimal;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {
    private String paymentCode;
    private PaymentStatus status;
    private PaymentMethod method;
    private BigDecimal amount;
    private String paymentUrl;
}
