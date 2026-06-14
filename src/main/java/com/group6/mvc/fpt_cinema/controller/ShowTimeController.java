package com.group6.mvc.fpt_cinema.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.showtime.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.response.showtime.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

@RestController
@RequestMapping("/api/showtimes")
public class ShowTimeController {

    private final ShowtimeService showtimeService;

    public ShowTimeController(ShowtimeService showtimeService){
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public ApiResponse<List<ShowtimeResponse>> getAllShowtimes(
        @RequestParam(required = false) Integer movieId,
        @RequestParam(required = false) Integer roomId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        return ApiResponse.<List<ShowtimeResponse>>builder()
        .message("Showtimes retrived successfully")
        .result(showtimeService.getAllShowtimes(movieId, roomId, date))
        .build();
    }


    @GetMapping("/{id}")
    public ApiResponse<ShowtimeResponse> getShowtimeById(@PathVariable Integer id){
        return ApiResponse.<ShowtimeResponse>builder()
        .message("Showtime retrived successfully")
        .result(showtimeService.getShowtimeById(id))
        .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ShowtimeResponse> createShowtime(@RequestBody ShowtimeRequest request){
        return ApiResponse.<ShowtimeResponse>builder()
        .message("Showtime created successfully")
        .result(showtimeService.createShowtime(request))
        .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ShowtimeResponse> updateShowtime(
        @PathVariable Integer id,
        @RequestBody ShowtimeRequest request
    ){
        return ApiResponse.<ShowtimeResponse>builder()
        .message("Showtime updated successfully")
        .result(showtimeService.updateShowtime(id, request))
        .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelShowtime(@PathVariable Integer id){
        showtimeService.cancelShowtime(id);
        return ApiResponse.<Void>builder()
        .message("Showtime cancelled successfully")
        .build();
    }
}
