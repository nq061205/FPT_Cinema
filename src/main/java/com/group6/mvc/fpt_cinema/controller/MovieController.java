package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/list")
    public ApiResponse<List<ViewMovieListResponse>> viewMovieList() {
        List<ViewMovieListResponse> response = movieService.getAllMovies();
        ApiResponse<List<ViewMovieListResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Movies retrieved successfully!");
        apiResponse.setResult(response);
        return apiResponse;
    }

    @PostMapping("/view")
    public ApiResponse<List<ViewMovieListResponse>> viewMovieList(
            @RequestBody ViewMovieListRequest request) {

        ApiResponse<List<ViewMovieListResponse>> response = new ApiResponse<>();

        response.setMessage(
                "Movies retrieved successfully!");

        response.setResult(
                movieService.viewMovieList(request));

        return response;
    }
}
