package com.group6.mvc.fpt_cinema.dto.report.response.payment;

import java.math.BigDecimal;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodItem {
    private PaymentMethod paymentMethod;
    private Integer transactionCount;
    private BigDecimal amount;

}
