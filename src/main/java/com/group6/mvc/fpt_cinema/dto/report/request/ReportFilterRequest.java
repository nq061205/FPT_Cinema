package com.group6.mvc.fpt_cinema.dto.report.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
