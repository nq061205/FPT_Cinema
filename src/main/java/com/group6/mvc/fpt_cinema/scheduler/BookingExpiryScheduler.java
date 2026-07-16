package com.group6.mvc.fpt_cinema.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.service.BookingService;

@Component
public class BookingExpiryScheduler {

    private final BookingService bookingService;

    public BookingExpiryScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 30000)
    public void expireStaleBookings() {
        bookingService.expireStaleBookings();
    }
}
