package com.group6.mvc.fpt_cinema.dto.payment.request;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    private String bookingCode;
    private PaymentMethod method;
    private String bankCode;
}