package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingChannelItem;
import com.group6.mvc.fpt_cinema.dto.report.response.booking.BookingTrendItem;
import com.group6.mvc.fpt_cinema.dto.report.response.customer.TopCustomerItem;
import com.group6.mvc.fpt_cinema.dto.report.response.movie.MoviePerformanceItem;
import com.group6.mvc.fpt_cinema.dto.report.response.payment.PaymentMethodItem;
import com.group6.mvc.fpt_cinema.dto.report.response.promotion.PromotionUsageItem;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.MovieRevenueItem;
import com.group6.mvc.fpt_cinema.dto.report.response.revenue.RevenueTrendItem;
import com.group6.mvc.fpt_cinema.repository.projection.BookingChannelProjection;
import com.group6.mvc.fpt_cinema.repository.projection.BookingTrendProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MoviePerformanceProjection;
import com.group6.mvc.fpt_cinema.repository.projection.MovieRevenueProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PaymentMethodProjection;
import com.group6.mvc.fpt_cinema.repository.projection.PromotionUsageProjection;
import com.group6.mvc.fpt_cinema.repository.projection.RevenueTrendProjection;
import com.group6.mvc.fpt_cinema.repository.projection.TopCustomerProjection;

/**
 * Maps a database projection row into the matching report item DTO.
 * Hand-written @Component using new + setters, same style as MovieMapper.
 */
@Component
public class ReportMapper {

    public BookingTrendItem toBookingTrendItem(BookingTrendProjection projection) {
        BookingTrendItem item = new BookingTrendItem();
        item.setPeriod(projection.getPeriod());
        item.setBookingCount(projection.getBookingCount());
        item.setCompletedCount(projection.getCompletedCount());
        item.setCancelledCount(projection.getCancelledCount());
        return item;
    }

    public BookingChannelItem toBookingChannelItem(BookingChannelProjection projection) {
        BookingChannelItem item = new BookingChannelItem();
        item.setChannel(projection.getChannel());
        item.setBookingCount(projection.getBookingCount());
        return item;
    }

    public RevenueTrendItem toRevenueTrendItem(RevenueTrendProjection projection) {
        RevenueTrendItem item = new RevenueTrendItem();
        item.setPeriod(projection.getPeriod());
        item.setRevenue(projection.getRevenue());
        item.setOrderCount(projection.getOrderCount());
        return item;
    }

    public MovieRevenueItem toMovieRevenueItem(MovieRevenueProjection projection) {
        MovieRevenueItem item = new MovieRevenueItem();
        item.setMovieTitle(projection.getMovieTitle());
        item.setRevenue(projection.getRevenue());
        return item;
    }

    public PaymentMethodItem toPaymentMethodItem(PaymentMethodProjection projection) {
        PaymentMethodItem item = new PaymentMethodItem();
        item.setPaymentMethod(projection.getPaymentMethod());
        item.setTransactionCount(projection.getTransactionCount());
        item.setAmount(projection.getAmount());
        return item;
    }

    public PromotionUsageItem toPromotionUsageItem(PromotionUsageProjection projection) {
        PromotionUsageItem item = new PromotionUsageItem();
        item.setPromotionCode(projection.getPromotionCode());
        item.setDiscountAmount(projection.getDiscountAmount());
        return item;
    }

    public TopCustomerItem toTopCustomerItem(TopCustomerProjection projection) {
        TopCustomerItem item = new TopCustomerItem();
        item.setCustomerName(projection.getCustomerName());
        item.setBookingCount(projection.getBookingCount());
        item.setSpending(projection.getSpending());
        return item;
    }

    public MoviePerformanceItem toMoviePerformanceItem(MoviePerformanceProjection projection) {
        MoviePerformanceItem item = new MoviePerformanceItem();
        item.setMovieTitle(projection.getMovieTitle());
        item.setTicketsSold(projection.getTicketsSold());
        item.setBookingCount(projection.getBookingCount());
        item.setRevenue(projection.getRevenue());
        item.setOccupancyRate(0.0);
        return item;
    }
}
