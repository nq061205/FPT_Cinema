package com.group6.mvc.fpt_cinema.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

/**
 * Compatibility endpoint for clients that use the branch-based quick-showtime URL.
 * The current data model represents a single cinema, so {@code branchId} is accepted
 * for URL compatibility but does not filter the result.
 */
@RestController
@RequestMapping("/api/v1/branches/{branchId}/showtimes")
public class QuickShowtimeController {

    private final ShowtimeService showtimeService;

    public QuickShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/quick")
    public ApiResponse<Page<ShowtimeResponse>> getQuickShowtimes(
            @PathVariable Integer branchId,
            @RequestParam(required = false) Integer movieId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "startTime") Pageable pageable) {
        return ApiResponse.<Page<ShowtimeResponse>>builder()
                .message("Quick showtimes retrieved successfully")
                .result(showtimeService.getAllShowtimes(movieId, roomId, date, status, pageable))
                .build();
    }
}
