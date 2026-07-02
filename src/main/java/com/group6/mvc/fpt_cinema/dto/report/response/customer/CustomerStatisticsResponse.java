package com.group6.mvc.fpt_cinema.dto.report.response.customer;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatisticsResponse {
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    private List<TopCustomerItem> topCustomers;

}
