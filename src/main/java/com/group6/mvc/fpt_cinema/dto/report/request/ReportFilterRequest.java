package com.group6.mvc.fpt_cinema.dto.report.request;

import java.time.LocalDate;

import com.group6.mvc.fpt_cinema.enums.ReportGroupBy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ReportFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportGroupBy groupBy;
}
