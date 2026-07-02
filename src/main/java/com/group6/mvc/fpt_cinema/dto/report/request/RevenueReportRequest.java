package com.group6.mvc.fpt_cinema.dto.report.request;

import com.group6.mvc.fpt_cinema.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportRequest extends ReportFilterRequest {
    private PaymentMethod paymentMethod;
    private Boolean completedBookingsOnly;
}
