package com.group6.mvc.fpt_cinema.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.dto.report.request.BookingReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.CustomerStatisticsRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.MovieReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.PaymentReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.PromotionReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.request.RevenueReportRequest;
import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingChannelItem;
import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingTrendItem;
import com.group6.mvc.fpt_cinema.dto.report.response.customer.CustomerStatisticsResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.customer.TopCustomerItem;
import com.group6.mvc.fpt_cinema.dto.report.response.movie.MoviePerformanceItem;
import com.group6.mvc.fpt_cinema.dto.report.response.movie.MovieReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.payment.PaymentMethodItem;
import com.group6.mvc.fpt_cinema.dto.report.response.payment.PaymentReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.promotion.PromotionReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.promotion.PromotionUsageItem;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.MovieRevenueItem;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.RevenueReportResponse;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.RevenueTrendItem;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.ReportMapper;
import com.group6.mvc.fpt_cinema.repository.ReportRepository;
import com.group6.mvc.fpt_cinema.repository.projection.MovieCapacityProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MoviePerformanceProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PaymentStatusCountProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PromotionSummaryProjection;
import com.group6.mvc.fpt_cinema.security.RoleIds;
import com.group6.mvc.fpt_cinema.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    @Override
    public BookingReportResponse getBookingReport(BookingReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        List<BookingTrendItem> trend = reportRepository
                .findBookingTrend(start, end, request.getMovieId(), request.getStatuses(), request.getChannels())
                .stream()
                .map(reportMapper::toBookingTrendItem)
                .toList();

        List<BookingChannelItem> channels = reportRepository
                .findBookingChannelDistribution(start, end, request.getMovieId(), request.getStatuses(),
                        request.getChannels())
                .stream()
                .map(reportMapper::toBookingChannelItem)
                .toList();

        int total = 0;
        int completed = 0;
        int cancelled = 0;
        for (BookingTrendItem item : trend) {
            total += item.getBookingCount();
            completed += item.getCompletedCount();
            cancelled += item.getCancelledCount();
        }

        BookingReportResponse response = new BookingReportResponse();
        response.setTotalBookings(total);
        response.setCompletedBookings(completed);
        response.setCancelledBookings(cancelled);
        response.setBookingTrend(trend);
        response.setChannelDistribution(channels);
        return response;
    }

    @Override
    public RevenueReportResponse getRevenueReport(RevenueReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();
        boolean completedOnly = Boolean.TRUE.equals(request.getCompletedBookingsOnly());

        List<RevenueTrendItem> trend = reportRepository
                .findRevenueTrend(start, end, request.getPaymentMethod(), completedOnly)
                .stream()
                .map(reportMapper::toRevenueTrendItem)
                .toList();

        List<MovieRevenueItem> movieRevenue = reportRepository
                .findMovieRevenue(start, end, request.getPaymentMethod(), completedOnly)
                .stream()
                .map(reportMapper::toMovieRevenueItem)
                .toList();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int orderCount = 0;
        for (RevenueTrendItem item : trend) {
            totalRevenue = totalRevenue.add(item.getRevenue());
            orderCount += item.getOrderCount();
        }
        BigDecimal averageOrderValue = orderCount == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

        RevenueReportResponse response = new RevenueReportResponse();
        response.setTotalRevenue(totalRevenue);
        response.setAverageOrderValue(averageOrderValue);
        response.setRevenueTrend(trend);
        response.setMovieRevenue(movieRevenue);
        return response;
    }

    @Override
    public PaymentReportResponse getPaymentReport(PaymentReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        PaymentStatusCountProjection counts = reportRepository
                .findPaymentStatusCounts(start, end, request.getPaymentStatus(), request.getPaymentMethod());

        List<PaymentMethodItem> methods = reportRepository
                .findPaymentMethodBreakdown(start, end, request.getPaymentMethod())
                .stream()
                .map(reportMapper::toPaymentMethodItem)
                .toList();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PaymentMethodItem item : methods) {
            totalAmount = totalAmount.add(item.getAmount());
        }

        PaymentReportResponse response = new PaymentReportResponse();
        response.setTotalTransactions(counts.getTotal());
        response.setSuccessfulTransactions(counts.getSuccessful() == null ? 0 : counts.getSuccessful());
        response.setFailedTransactions(counts.getFailed() == null ? 0 : counts.getFailed());
        response.setTotalAmount(totalAmount);
        response.setPaymentMethods(methods);
        return response;
    }

    @Override
    public PromotionReportResponse getPromotionReport(PromotionReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();
        String type = request.getPromotionType() == null ? null : request.getPromotionType().name();

        PromotionSummaryProjection summary = reportRepository
                .findPromotionSummary(start, end, request.getPromotionCode(), type);

        List<PromotionUsageItem> topPromotions = reportRepository
                .findPromotionUsage(start, end, request.getPromotionCode(), type)
                .stream()
                .limit(10)
                .map(reportMapper::toPromotionUsageItem)
                .toList();

        PromotionReportResponse response = new PromotionReportResponse();
        response.setTotalPromotions(summary.getTotalPromotions() == null ? 0 : summary.getTotalPromotions());
        response.setTotalUsage(summary.getTotalUsage() == null ? 0 : summary.getTotalUsage());
        response.setTotalDiscountAmount(
                summary.getTotalDiscountAmount() == null ? BigDecimal.ZERO : summary.getTotalDiscountAmount());
        response.setTopPromotions(topPromotions);
        return response;
    }

    @Override
    public MovieReportResponse getMovieReport(MovieReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        Map<Integer, Integer> capacityByMovie = new HashMap<>();
        for (MovieCapacityProjection capacity : reportRepository.findMovieCapacity(start, end,
                request.getMovieStatus())) {
            capacityByMovie.put(capacity.getMovieId(), capacity.getCapacity() == null ? 0 : capacity.getCapacity());
        }

        List<MoviePerformanceItem> movies = new ArrayList<>();
        int totalTickets = 0;
        int totalCapacity = 0;
        for (MoviePerformanceProjection projection : reportRepository.findMoviePerformance(start, end,
                request.getMovieStatus())) {
            int tickets = projection.getTicketsSold() == null ? 0 : projection.getTicketsSold();
            int capacity = capacityByMovie.getOrDefault(projection.getMovieId(), 0);
            totalTickets += tickets;
            totalCapacity += capacity;

            MoviePerformanceItem item = reportMapper.toMoviePerformanceItem(projection);
            item.setOccupancyRate(capacity == 0 ? 0.0 : Math.round(tickets * 10000.0 / capacity) / 100.0);
            movies.add(item);
        }

        MovieReportResponse response = new MovieReportResponse();
        response.setTotalMovies(movies.size());
        response.setTotalTicketsSold(totalTickets);
        response.setAverageOccupancyRate(
                totalCapacity == 0 ? 0.0 : Math.round(totalTickets * 10000.0 / totalCapacity) / 100.0);
        response.setTopMovies(movies.stream().limit(10).toList());
        return response;
    }

    @Override
    public CustomerStatisticsResponse getCustomerStatistics(CustomerStatisticsRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null
                || request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().plusDays(1).atStartOfDay();

        List<TopCustomerItem> topCustomers = reportRepository
                .findTopCustomers(start, end, request.getMembershipLevel(), request.getUserStatus())
                .stream()
                .limit(10)
                .map(reportMapper::toTopCustomerItem)
                .toList();

        int returning = reportRepository
                .findReturningCustomerIds(start, end, request.getMembershipLevel(), request.getUserStatus())
                .size();

        CustomerStatisticsResponse response = new CustomerStatisticsResponse();
        response.setTotalCustomers(
                reportRepository.countCustomers(RoleIds.CUSTOMER, request.getMembershipLevel(),
                        request.getUserStatus()));
        response.setNewCustomers(
                reportRepository.countNewCustomers(RoleIds.CUSTOMER, start, end, request.getMembershipLevel(),
                        request.getUserStatus()));
        response.setReturningCustomers(returning);
        response.setTopCustomers(topCustomers);
        return response;
    }
}
