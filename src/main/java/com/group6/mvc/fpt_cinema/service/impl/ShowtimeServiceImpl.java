package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.BatchShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.request.ShowtimeRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewMovieListRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewShowTimeListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ShowtimeResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewMovieListResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewShowTimeListResponse;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Room;
import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;
import com.group6.mvc.fpt_cinema.enums.ShowtimeStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.IShowtimeMapper;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.ShowtimeService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

    private final ThreadPoolTaskScheduler taskScheduler;


    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository,
                               MovieRepository movieRepository,
                               RoomRepository roomRepository,
                               IShowtimeMapper IShowtimeMapper,
                               BookingRepository bookingRepository,
                               ThreadPoolTaskScheduler taskScheduler) {
        super(showtimeRepository);
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.IShowtimeMapper = IShowtimeMapper;
        this.bookingRepository = bookingRepository;
        this.taskScheduler = taskScheduler;
    }

    private static final ShowtimeStatus STATUS_CANCELLED = ShowtimeStatus.CANCELLED;
    private static final BookingStatus BOOKING_CONFIRMED = BookingStatus.CONFIRMED;

    @Value("${cinema.opening-time:08:00}")
    private String openingTime;

    @Value("${cinema.closing-time:23:00}")
    private String closingTime;

    @Value("${cinema.default-cleaning-buffer-minutes:15}")
    private int defaultCleaningBuffer;

    private static final int MAX_BATCH_SIZE = 100;
    private static final int MAX_SHOWTIMES_PER_ROOM_PER_DAY = 8;



    @Override
    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        if (movie.getStatus() != MovieStatus.NOW_SHOWING) {
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

        
        scheduleShowtimeEndJob(saved);

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
        response.setSeatPrices(buildSeatPrices(showtime.getBasePrice()));

        response.setStatus(resolveEffectiveStatus(showtime).name());
        return response;
    }


    private ShowtimeStatus resolveEffectiveStatus(Showtime showtime) {

        if (showtime.getStatus() == ShowtimeStatus.CANCELLED) {
            return ShowtimeStatus.CANCELLED;
        }


        LocalDateTime endTime = showtime.getStartTime()
                .plusMinutes(showtime.getMovie().getDurationMinutes());
        if (endTime.isBefore(LocalDateTime.now())) {
            return ShowtimeStatus.FINISHED;
        }


        return showtime.getStatus();
    }


    private Map<String, BigDecimal> buildSeatPrices(BigDecimal basePrice) {
        return Map.of(
            "NORMAL", basePrice,
            "VIP",    basePrice.multiply(new BigDecimal("1.5")),
            "COUPLE", basePrice.multiply(new BigDecimal("1.8")),
            "PREMIUM",basePrice.multiply(new BigDecimal("2.0"))
        );
    }

    private void checkOverlap(Integer roomId, LocalDateTime newStart, LocalDateTime newEnd, Integer excludeId) {
        List<Showtime> existing = showtimeRepository.findActiveShowtimesByRoomId(roomId);

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


        ShowtimeStatus effectiveStatus = resolveEffectiveStatus(showtime);
        if (effectiveStatus == ShowtimeStatus.CANCELLED
                || effectiveStatus == ShowtimeStatus.FINISHED) {
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

        
        scheduleShowtimeEndJob(saved);

        return toResponse(saved);

    }

    @Override
    @Transactional
    public void cancelShowtime(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));


        ShowtimeStatus effectiveStatus = resolveEffectiveStatus(showtime);
        if (effectiveStatus == ShowtimeStatus.FINISHED) {
            throw new AppException(ErrorCode.SHOWTIME_ALREADY_FINISHED);
        }

        if (effectiveStatus == ShowtimeStatus.CANCELLED) {
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
    public Page<ShowtimeResponse> getAllShowtimes(Integer movieId, Integer roomId, LocalDate date,String status,   Pageable pageable) {
        LocalDateTime startDate = (date != null) ? date.atStartOfDay() : null;
        LocalDateTime endDate = (date != null) ? date.plusDays(1).atStartOfDay() : null;

        return showtimeRepository.findFiltered(movieId, roomId, startDate, endDate, status, pageable)
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

    @Override
    @Transactional
    public List<ShowtimeResponse> createBatch(BatchShowtimeRequest request) {

          long totalShowtimes = request.getRoomIds().size()
            * request.getDailyStartTimes().size()
            * (request.getEndDate().toEpochDay() - request.getStartDate().toEpochDay() + 1);

        if (totalShowtimes > MAX_BATCH_SIZE) {
            throw new AppException(ErrorCode.BATCH_TOO_LARGE);
        }

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        List<Showtime> showtimesToSave = new ArrayList<>();

        for(Integer roomId : request.getRoomIds()){
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

            if(!"ACTIVE".equals(room.getStatus())){
                throw new AppException(ErrorCode.ROOM_NOT_ACTIVE);
            }

            for(LocalDate date = request.getStartDate();
            !date.isAfter(request.getEndDate());
            date = date.plusDays(1)){
                long existingCount = showtimeRepository.findFiltered(
                                null,
                                roomId,
                                date.atStartOfDay(),date.plusDays(1).atStartOfDay(),
                                null,
                                Pageable.unpaged()
                ).getTotalElements();

                if(existingCount + request.getDailyStartTimes().size() > MAX_SHOWTIMES_PER_ROOM_PER_DAY){
                    throw new AppException(ErrorCode.ROOM_DAILY_LIMIT_EXCEEDED);
                }

                for(LocalTime time : request.getDailyStartTimes()){
                    LocalDateTime startTime = LocalDateTime.of(date, time);

                    if(startTime.isBefore(LocalDateTime.now())){
                        throw new AppException(ErrorCode.SHOWTIME_IN_PAST);

                    }

                    validateOperatingHours(startTime, movie.getDurationMinutes());

                    int buffer = request.getCleaningBufferMinutes() != null
                            ? request.getCleaningBufferMinutes()
                            :defaultCleaningBuffer;

                    checkOverlap(roomId, startTime, startTime.plusMinutes(movie.getDurationMinutes() + buffer), null);

                    Showtime showtime = new Showtime();
                    showtime.setMovie(movie);
                    showtime.setRoom(room);
                    showtime.setStartTime(startTime);
                    showtime.setBasePrice(request.getBasePrice());
                    showtime.setCleaningBufferMinutes(buffer);
                    showtime.setStatus(ShowtimeStatus.OPEN);

                    Showtime saved = showtimeRepository.save(showtime);
                    showtimesToSave.add(showtime);
                }

            }
        }

        List<Showtime> savedShowtimes = showtimeRepository.saveAll(showtimesToSave);

        
        for (Showtime saved : savedShowtimes) {
            scheduleShowtimeEndJob(saved);
        }

        return savedShowtimes.stream()
                .map(this::toResponse)
                .toList();

    }

    @Override
    public List<LocalDate> suggestSlots(Integer movieId, Integer roomId, LocalDate date) {
        return List.of();
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @org.springframework.transaction.annotation.Transactional
    public void recoverShowtimeJobs() {
        LocalDateTime now = LocalDateTime.now();
       
        int updated = showtimeRepository.updateMissedShowtimes(now);
        System.out.println("Recovered/Updated " + updated + " missed showtimes to FINISHED.");

      
        List<Showtime> futureShowtimes = showtimeRepository.findFutureActiveShowtimes(now);
        for (Showtime showtime : futureShowtimes) {
            scheduleShowtimeEndJob(showtime);
        }
        System.out.println("Rescheduled " + futureShowtimes.size() + " active showtime jobs.");
    }

    public void scheduleShowtimeEndJob(Showtime showtime) {
        LocalDateTime endTime = showtime.getStartTime()
                .plusMinutes(showtime.getMovie().getDurationMinutes());

        //neu thoi gian đã qua thì cập nhật ngay
        if (endTime.isBefore(LocalDateTime.now())) {
            showtimeRepository.updateStatusToFinished(showtime.getId());
            return;
        }

        
        Instant executionTime = endTime.atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(() -> {
            showtimeRepository.updateStatusToFinished(showtime.getId());
        }, executionTime);
    }

}
