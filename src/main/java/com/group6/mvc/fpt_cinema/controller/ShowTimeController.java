package com.group6.mvc.fpt_cinema.controller;

import java.time.LocalDate;
import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.BatchShowtimeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.group6.mvc.fpt_cinema.dto.request.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewShowTimeListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewShowTimeListResponse;
import com.group6.mvc.fpt_cinema.service.SeatService;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

@RestController
@RequestMapping("/api/showtimes")
public class ShowTimeController {

    private final ShowtimeService showtimeService;
    private final SeatService seatService;

    public ShowTimeController(ShowtimeService showtimeService, SeatService seatService) {
        this.showtimeService = showtimeService;
        this.seatService = seatService;
    }

    @GetMapping
    public ApiResponse<Page<ShowtimeResponse>> getAllShowtimes(
        @RequestParam(required = false) Integer movieId,
        @RequestParam(required = false) Integer roomId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false, defaultValue = "OPEN") String status,
        @PageableDefault(size = 20, sort = "startTime") Pageable pageable
    ){
        return ApiResponse.<Page<ShowtimeResponse>>builder()
        .message("Showtimes retrieved successfully")
        .result(showtimeService.getAllShowtimes(movieId, roomId, date, status, pageable))
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
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('SHOWTIME_MANAGE')")
    public ApiResponse<ShowtimeResponse> createShowtime(@RequestBody ShowtimeRequest request){
        return ApiResponse.<ShowtimeResponse>builder()
        .message("Showtime created successfully")
        .result(showtimeService.createShowtime(request))
        .build();
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('SHOWTIME_MANAGE')")
    public ApiResponse<List<ShowtimeResponse>> createBatch(
            @RequestBody BatchShowtimeRequest request
            ){
        return ApiResponse.<List<ShowtimeResponse>>builder()
                .message("Batch create successfully")
                .result(showtimeService.createBatch(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('SHOWTIME_MANAGE')")
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
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('SHOWTIME_MANAGE')")
    public ApiResponse<Void> cancelShowtime(@PathVariable Integer id){
        showtimeService.cancelShowtime(id);
        return ApiResponse.<Void>builder()
        .message("Showtime cancelled successfully")
        .build();
    }


    @GetMapping("/{id}/seats")
    public ApiResponse<ViewSeatMapResponse> getSeatMap(@PathVariable Integer id) {
        ViewSeatMapRequest request = new ViewSeatMapRequest();
        request.setShowtimeId(id);
        return ApiResponse.<ViewSeatMapResponse>builder()
                .message("Seat map retrieved successfully")
                .result(seatService.viewSeatMap(request))
                .build();
    }

    @PostMapping("/list")
    public ApiResponse<List<ViewShowTimeListResponse>> getShowtimesByCriteria(
        @RequestBody ViewShowTimeListRequest request,
        @PageableDefault(size = 5, sort = "startTime") Pageable pageable
    ){
        List<ViewShowTimeListResponse> response = showtimeService.getShowTimesList(request);
        ApiResponse<List<ViewShowTimeListResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Showtimes list retrieved successfully!");
        apiResponse.setResult(response);
        return apiResponse;
    }
}
