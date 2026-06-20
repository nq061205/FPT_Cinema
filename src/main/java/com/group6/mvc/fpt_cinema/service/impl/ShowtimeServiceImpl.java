package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewShowTimeListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewShowTimeListResponse;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Room;
import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.IShowtimeMapper;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ShowtimeServiceImpl
        extends AbstractCrudService<Showtime, Integer>
        implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private IShowtimeMapper IShowtimeMapper;

    public ShowtimeServiceImpl(ShowtimeRepository repository, IShowtimeMapper IShowtimeMapper) {
        super(repository);
        this.showtimeRepository = repository;
        this.IShowtimeMapper = IShowtimeMapper;
    }

    @Override
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.SHOWTIME_IN_PAST);
        }

        if (request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_PRICE);
        }

        checkOverlap(request.getRoomId(), request.getStartTime(),
                request.getStartTime().plusMinutes(movie.getDurationMinutes()), null);

        Showtime showtime = IShowtimeMapper.toEntity(request);
        showtime.setMovie(movie);
        showtime.setRoom(room);
        Showtime saved = showtimeRepository.save(showtime);

        return toResponse(saved);

    }

    private ShowtimeResponse toResponse(Showtime showtime) {
        ShowtimeResponse response = IShowtimeMapper.toResponse(showtime);
        response.setEndTime(showtime.getStartTime()
                .plusMinutes(showtime.getMovie().getDurationMinutes()));
        return response;
    }

    private void checkOverlap(Integer roomId, LocalDateTime newStart, LocalDateTime newEnd, Integer excludeId) {
        List<Showtime> existing = showtimeRepository.findByRoomIdAndStatusNotIn(roomId,
                List.of("CANCELLED", "FINISHED"));

        for (Showtime s : existing) {
            if (excludeId != null && s.getId().equals(excludeId)) {
                continue;
            }

            LocalDateTime existingEnd = s.getStartTime()
                    .plusMinutes(s.getMovie().getDurationMinutes());

            if (newStart.isBefore(existingEnd) && s.getStartTime().isBefore(newEnd)) {
                throw new AppException(ErrorCode.SHOWTIME_OVERLAP);
            }
        }
    }

    @Override
    @Transactional
    public ShowtimeResponse updateShowtime(Integer id, ShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        Movie movie = showtime.getMovie();
        if (request.getMovieId() != null) {
            movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
            showtime.setMovie(movie);
        }

        Room room = showtime.getRoom();
        if (request.getRoomId() != null) {
            room = roomRepository.findById(
                    request.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
            showtime.setRoom(room);
        }

        LocalDateTime newStart = showtime.getStartTime();
        if (request.getStartTime() != null) {
            if (request.getStartTime().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.SHOWTIME_IN_PAST);
            }

            newStart = request.getStartTime();
            showtime.setStartTime(newStart);
        }

        if (request.getBasePrice() != null) {
            if (request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AppException(ErrorCode.INVALID_PRICE);
            }
            showtime.setBasePrice(request.getBasePrice());
        }

        checkOverlap(showtime.getRoom().getId(), newStart, newStart.plusMinutes(movie.getDurationMinutes()), id);

        Showtime saved = showtimeRepository.save(showtime);
        return toResponse(saved);

    }

    @Override
    @Transactional
    public void cancelShowtime(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        showtime.setStatus("CANCELLED");
        showtimeRepository.save(showtime);
    }

    @Override
    public ShowtimeResponse getShowtimeById(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        return toResponse(showtime);
    }

    @Override
    public Page<ShowtimeResponse> getAllShowtimes(Integer movieId, Integer roomId, LocalDate date, Pageable pageable) {
        LocalDateTime startDate = (date != null) ? date.atStartOfDay() : null;
        LocalDateTime endDate = (date != null) ? date.plusDays(1).atStartOfDay() : null;

        return showtimeRepository.findFiltered(List.of("CANCELLED"), movieId, roomId, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    @Override
    public List<ViewShowTimeListResponse> getShowTimesList(ViewShowTimeListRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize());
        Page<Showtime> showtimePage = showtimeRepository.findAll(pageable);
        return showtimePage.getContent()
                .stream()
                .map(IShowtimeMapper::toViewResponse)
                .toList();
    }
}
