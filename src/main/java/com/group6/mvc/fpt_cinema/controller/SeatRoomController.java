package com.group6.mvc.fpt_cinema.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.BatchUpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.GenerateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.response.SeatResponse;
import com.group6.mvc.fpt_cinema.service.SeatService;

@RestController
@RequestMapping("/api/rooms/{roomId}/seats")
public class SeatRoomController {

    private final SeatService seatService;

    public SeatRoomController(SeatService seatService){
        this.seatService = seatService;
    }

    @GetMapping
    public ApiResponse<Page<SeatResponse>> getSeats(
        @PathVariable Integer roomId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String seatType,
        @PageableDefault(size = 50, sort = {"seatRow", "seatNumber"}) Pageable pageable
    ){
        return ApiResponse.<Page<SeatResponse>>builder()
        .message("Seat retrieved successfully")
        .result(seatService.getSeatsByRoom(roomId, status, seatType, pageable))
        .build();

    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<SeatResponse>> generateSeats(
        @PathVariable Integer roomId,
        @RequestBody GenerateSeatRequest request
    ){
        return ApiResponse.<List<SeatResponse>>builder()
        .message("Seats generated successfully")
        .result(seatService.generateSeats(roomId, request))
        .build();
    }


    @PutMapping("/{seatId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<SeatResponse> updateSeat(
        @PathVariable Integer roomId,
        @PathVariable Integer seatId,
        @RequestBody UpdateSeatRequest request
    ){
        return ApiResponse.<SeatResponse>builder()
        .message("seat update success fully")
        .result(seatService.updateSeat(roomId, seatId, request))
        .build();
    }


    @PatchMapping("/batch")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<SeatResponse>> batchUpdateSeats(
        @PathVariable Integer roomId,
        @RequestBody BatchUpdateSeatRequest request
    ) {
        return ApiResponse.<List<SeatResponse>>builder()
        .message("Seats updated successfully")
        .result(seatService.batchUpdateSeats(roomId, request))
        .build();
    }





}
