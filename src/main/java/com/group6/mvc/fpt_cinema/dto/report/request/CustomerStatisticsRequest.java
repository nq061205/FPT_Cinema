package com.group6.mvc.fpt_cinema.dto.report.request;

import com.group6.mvc.fpt_cinema.enums.MembershipLevel;
import com.group6.mvc.fpt_cinema.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStatisticsRequest extends ReportFilterRequest {
    private MembershipLevel membershipLevel;
    private UserStatus userStatus;
}
