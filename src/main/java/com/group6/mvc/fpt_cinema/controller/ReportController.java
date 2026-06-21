package com.group6.mvc.fpt_cinema.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
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
import com.group6.mvc.fpt_cinema.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/booking")
    public ApiResponse<BookingReportResponse> bookingReport(
            @RequestBody BookingReportRequest request) {
        ApiResponse<BookingReportResponse> response = new ApiResponse<>();
        response.setMessage("Booking report generated successfully!");
        response.setResult(reportService.getBookingReport(request));
        return response;
    }

    @PostMapping("/payment")
    public ApiResponse<PaymentReportResponse> paymentReport(
            @RequestBody PaymentReportRequest request) {
        ApiResponse<PaymentReportResponse> response = new ApiResponse<>();
        response.setMessage("Payment report generated successfully!");
        response.setResult(reportService.getPaymentReport(request));
        return response;
    }

    @PostMapping("/revenue")
    public ApiResponse<RevenueReportResponse> revenueReport(
            @RequestBody RevenueReportRequest request) {
        ApiResponse<RevenueReportResponse> response = new ApiResponse<>();
        response.setMessage("Revenue report generated successfully!");
        response.setResult(reportService.getRevenueReport(request));
        return response;
    }

    @PostMapping("/customer")
    public ApiResponse<CustomerStatisticsResponse> customerStatistics(
            @RequestBody CustomerStatisticsRequest request) {
        ApiResponse<CustomerStatisticsResponse> response = new ApiResponse<>();
        response.setMessage("Customer statistics generated successfully!");
        response.setResult(reportService.getCustomerStatistics(request));
        return response;
    }

    @PostMapping("/promotion")
    public ApiResponse<PromotionReportResponse> promotionReport(
            @RequestBody PromotionReportRequest request) {
        ApiResponse<PromotionReportResponse> response = new ApiResponse<>();
        response.setMessage("Promotion report generated successfully!");
        response.setResult(reportService.getPromotionReport(request));
        return response;
    }

    @PostMapping("/movie")
    public ApiResponse<MovieReportResponse> movieReport(
            @RequestBody MovieReportRequest request) {
        ApiResponse<MovieReportResponse> response = new ApiResponse<>();
        response.setMessage("Movie report generated successfully!");
        response.setResult(reportService.getMovieReport(request));
        return response;
    }
}
