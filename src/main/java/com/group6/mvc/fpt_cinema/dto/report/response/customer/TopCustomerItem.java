package com.group6.mvc.fpt_cinema.dto.report.response.customer;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerItem {
    private String customerName;
    private Integer bookingCount;
    private BigDecimal spending;
}
