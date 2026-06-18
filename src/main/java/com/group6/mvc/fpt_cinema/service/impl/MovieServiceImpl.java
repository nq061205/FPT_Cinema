package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.CreateMovieRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateMovieResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.mapper.MovieMapper;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.service.MovieService;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl
                extends AbstractCrudService<Movie, Integer>
                implements MovieService {

        private final MovieRepository movieRepository;
        private final MovieMapper movieMapper;

        public MovieServiceImpl(
                        MovieRepository movieRepository,
                        MovieMapper movieMapper) {

                super(movieRepository);

                this.movieRepository = movieRepository;
                this.movieMapper = movieMapper;
        }

        @Override
        public List<ViewMovieListResponse> getAllMovies() {
                List<Movie> movies = movieRepository.findAll();
                return movies.stream()
                                .map(movieMapper::toMovieResponse)
                                .toList();
        }

        @Override
        public List<ViewMovieListResponse> viewMovieList(
                        ViewMovieListRequest request) {

                Pageable pageable = PageRequest.of(
                                request.getPage(),
                                request.getSize());

                Page<Movie> moviePage = movieRepository.findAll(pageable);

                return moviePage.getContent()
                                .stream()
                                .map(movieMapper::toMovieResponse)
                                .toList();
        }

        @Override
        @Transactional
        public CreateMovieResponse createMovie(CreateMovieRequest request) {
                Movie movie = movieMapper.toMovie(request);
                Movie savedMovie = movieRepository.save(movie);
                return movieMapper.toCreateMovieResponse(savedMovie);
        }
}
