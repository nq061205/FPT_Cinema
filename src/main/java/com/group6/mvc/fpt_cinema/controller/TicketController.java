package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.CheckInTicketRequest;
import com.group6.mvc.fpt_cinema.dto.request.TicketLookupRequest;
import com.group6.mvc.fpt_cinema.dto.response.CheckInTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.TicketLookupResponse;
import com.group6.mvc.fpt_cinema.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ApiResponse<CheckInTicketResponse> checkIn(
            @Valid @RequestBody CheckInTicketRequest request) {

        ApiResponse<CheckInTicketResponse> response = new ApiResponse<>();
        response.setMessage("Ticket checked-in successfully!");
        response.setResult(ticketService.checkIn(request));
        return response;
    }

    @PostMapping("/lookup")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ApiResponse<TicketLookupResponse> lookup(
            @Valid @RequestBody TicketLookupRequest request) {

        ApiResponse<TicketLookupResponse> response = new ApiResponse<>();
        response.setMessage("Ticket lookup successful!");
        response.setResult(ticketService.lookup(request));
        return response;
    }
}
