package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.report.request.BookingReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.CustomerStatisticsRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.MovieReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.PaymentReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.PromotionReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.RevenueReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.customer.CustomerStatisticsResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.movie.MovieReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.payment.PaymentReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.promotion.PromotionReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.RevenueReportResponse;

public interface ReportService {

    BookingReportResponse getBookingReport(BookingReportRequest request);

    PaymentReportResponse getPaymentReport(PaymentReportRequest request);

    RevenueReportResponse getRevenueReport(RevenueReportRequest request);

    CustomerStatisticsResponse getCustomerStatistics(CustomerStatisticsRequest request);

    PromotionReportResponse getPromotionReport(PromotionReportRequest request);

    MovieReportResponse getMovieReport(MovieReportRequest request);
}
