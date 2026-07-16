package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.CreateMovieRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateMovieResponse;
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

    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('MOVIE_CREATE')")
    public ApiResponse<CreateMovieResponse> createMovie(
            @RequestBody CreateMovieRequest request) {

        ApiResponse<CreateMovieResponse> response = new ApiResponse<>();

        response.setMessage(
                "Movie created successfully!");

        response.setResult(
                movieService.createMovie(request));

        return response;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('MOVIE_UPDATE')")
    public ApiResponse<CreateMovieResponse> updateMovie(
            @PathVariable Integer id,
            @RequestBody CreateMovieRequest request) {

        ApiResponse<CreateMovieResponse> response = new ApiResponse<>();
        response.setMessage("Movie updated successfully!");
        response.setResult(movieService.updateMovie(id, request));
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasAuthority('MOVIE_DELETE')")
    public ApiResponse<Void> deleteMovie(@PathVariable Integer id) {
        movieService.deleteById(id);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Movie deleted successfully!");
        return response;
    }
}
