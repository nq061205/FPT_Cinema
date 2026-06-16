package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.service.SeatService;

@RestController
@RequestMapping("/api/seat")
public class SeatController {

    private SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("/list")
    public ApiResponse<ViewSeatMapResponse> viewSeatMap(
            @RequestBody ViewSeatMapRequest request) {

        ApiResponse<ViewSeatMapResponse> response = new ApiResponse<>();

        response.setMessage(
                "Seat map retrieved successfully!");

        response.setResult(
                seatService.viewSeatListByRoom(
                        request.getRoomId(),
                        request));

        return response;
    }
}
