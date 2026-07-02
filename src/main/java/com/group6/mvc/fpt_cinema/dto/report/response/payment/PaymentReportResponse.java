package com.group6.mvc.fpt_cinema.dto.report.response.payment;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReportResponse {
   private Integer totalTransactions;
   private Integer successfulTransactions;
   private Integer failedTransactions;
   private BigDecimal totalAmount;
   private List<PaymentMethodItem> paymentMethods;
}
