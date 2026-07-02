package com.group6.mvc.fpt_cinema.dto.report.request;

import com.group6.mvc.fpt_cinema.enums.MovieStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieReportRequest extends ReportFilterRequest {
    private MovieStatus movieStatus;

}
