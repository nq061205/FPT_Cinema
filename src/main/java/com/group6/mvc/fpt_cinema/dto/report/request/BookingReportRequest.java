package com.group6.mvc.fpt_cinema.dto.report.request;

import java.util.List;

import com.group6.mvc.fpt_cinema.enums.BookingChannel;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingReportRequest extends ReportFilterRequest {
    private List<BookingStatus> statuses;
    private List<BookingChannel> channels;
    private Integer movieId;
}
