package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.CreateBookingRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewBookingHistoryRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.service.BookingService;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ApiResponse<CreateBookingResponse> createBooking(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateBookingRequest request) {

        ApiResponse<CreateBookingResponse> response = new ApiResponse<>();
        response.setMessage("Booking created successfully!");
        response.setResult(bookingService.createBooking(getIntegerClaim(jwt, "userId"), request));
        return response;
    }

    @PostMapping("/list")
    public ApiResponse<List<ViewBookingHistoryResponse>> viewBookingList(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) ViewBookingHistoryRequest request) {

        ApiResponse<List<ViewBookingHistoryResponse>> response = new ApiResponse<>();
        response.setMessage("Booking history retrieved successfully!");
        response.setResult(bookingService.getBookingHistory(getIntegerClaim(jwt, "userId"), request));
        return response;
    }

    private Integer getIntegerClaim(Jwt jwt, String claimName) {
        Number value = jwt.getClaim(claimName);
        return value == null ? null : value.intValue();
    }
}
