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
import com.group6.mvc.fpt_cinema.enums.MovieStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.IShowtimeMapper;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ShowtimeServiceImpl
        extends AbstractCrudService<Showtime, Integer>
        implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    private final MovieRepository movieRepository;

    private final RoomRepository roomRepository;

    private final IShowtimeMapper IShowtimeMapper;
    private final BookingRepository bookingRepository;


    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository,
                               MovieRepository movieRepository,
                               RoomRepository roomRepository,
                               IShowtimeMapper IShowtimeMapper,
                               BookingRepository bookingRepository) {
        super(showtimeRepository);
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.IShowtimeMapper = IShowtimeMapper;
        this.bookingRepository = bookingRepository;
    }

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_SOLD_OUT = "SOLD_OUT";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_FINISHED = "FINISHED";
    private static final String BOOKING_CONFIRMED = "CONFIRMED";
    private static final Set<String> TERMINAL_STATUSES = Set.of(STATUS_CANCELLED, STATUS_FINISHED);
    private static final Set<String> INACTIVE_STATUSES = Set.of(STATUS_CANCELLED, STATUS_FINISHED);

    @Value("${cinema.opening-time:08:00}")
    private String openingTime;

    @Value("${cinema.closing-time:23:00}")
    private String closingTime;

    @Value("${cinema.default-cleaning-buffer-minutes:15}")
    private int defaultCleaningBuffer;



    @Override
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if(movie.getStatus() != MovieStatus.NOW_SHOWING){
            throw new AppException(ErrorCode.MOVIE_NOT_SHOWING);
        }

        if(!"ACTIVE".equals(room.getStatus())){
            throw new AppException(ErrorCode.ROOM_NOT_ACTIVE);
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.SHOWTIME_IN_PAST);
        }

        validateOperatingHours(request.getStartTime(), movie.getDurationMinutes());

        if(request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new AppException(ErrorCode.INVALID_PRICE);
        }

        int buffer = request.getCleaningBufferMinutes() != null
                ? request.getCleaningBufferMinutes()
                : defaultCleaningBuffer;



        checkOverlap(request.getRoomId(), request.getStartTime(),
                request.getStartTime().plusMinutes(movie.getDurationMinutes() + buffer), null);

        Showtime showtime = IShowtimeMapper.toEntity(request);
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setCleaningBufferMinutes(buffer);
        Showtime saved = showtimeRepository.save(showtime);

        return toResponse(saved);

    }

    private void validateOperatingHours(LocalDateTime startTime, int durationMinutes) {
        LocalTime opening = LocalTime.parse(openingTime.trim());
        LocalTime closing = LocalTime.parse(closingTime.trim());

        
        if (startTime.toLocalTime().isBefore(opening)) {
            throw new AppException(ErrorCode.SHOWTIME_OUTSIDE_HOURS);
        }

        
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        LocalDateTime closingDateTime = startTime.toLocalDate().atTime(closing);
        if (endTime.isAfter(closingDateTime)) {
            throw new AppException(ErrorCode.SHOWTIME_OUTSIDE_HOURS);
        }
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
                    .plusMinutes(s.getMovie().getDurationMinutes())
                    .plusMinutes(s.getCleaningBufferMinutes());

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

        if(TERMINAL_STATUSES.contains(showtime.getStatus())){
            throw new AppException(ErrorCode.SHOWTIME_CANNOT_UPDATE);
        }

        long confirmedBookings = bookingRepository.countByShowtimeIdAndStatus(id, BOOKING_CONFIRMED);
        if(confirmedBookings > 0){
            throw new AppException(ErrorCode.SHOWTIME_HAS_ACTIVE_BOOKINGS);
        }

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

        int buffer = request.getCleaningBufferMinutes() != null
                ? request.getCleaningBufferMinutes()
                : showtime.getCleaningBufferMinutes();

        checkOverlap(showtime.getRoom().getId(), newStart, newStart.plusMinutes(movie.getDurationMinutes() + buffer), id);

        Showtime saved = showtimeRepository.save(showtime);
        return toResponse(saved);

    }

    @Override
    @Transactional
    public void cancelShowtime(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        if(STATUS_FINISHED.equals(showtime.getStatus())){
            throw new AppException(ErrorCode.SHOWTIME_ALREADY_FINISHED);
        }

        if(STATUS_CANCELLED.equals(showtime.getStatus())){
            throw new AppException(ErrorCode.SHOWTIME_ALREADY_CANCELLED);
        }

        long confirmedBookings = bookingRepository.countByShowtimeIdAndStatus(id, BOOKING_CONFIRMED);
        if(confirmedBookings > 0) {
            throw new AppException(ErrorCode.SHOWTIME_HAS_ACTIVE_BOOKINGS);
        }

        showtime.setStatus(STATUS_CANCELLED);
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

    private boolean canTransaction(String from, String to){
        if(TERMINAL_STATUSES.contains(from)){
            return false;
        }

        return  true;
    }
}
