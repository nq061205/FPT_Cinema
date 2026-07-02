package com.group6.mvc.fpt_cinema.dto.report.request;

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
public class PaymentReportRequest extends ReportFilterRequest {
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
}
