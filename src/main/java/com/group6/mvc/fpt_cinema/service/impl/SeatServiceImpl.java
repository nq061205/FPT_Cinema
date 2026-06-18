package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.BatchUpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.GenerateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.response.SeatResponse;
import com.group6.mvc.fpt_cinema.entity.Room;
import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.SeatStatus;
import com.group6.mvc.fpt_cinema.enums.SeatType;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.SeatMapper;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.service.SeatService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatServiceImpl
        extends AbstractCrudService<Seat, Integer>
        implements SeatService {

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;
    private final SeatMapper seatMapper;



    public SeatServiceImpl(SeatRepository seatRepository, RoomRepository roomRepository, SeatMapper seatMapper) {
        super(seatRepository);
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
        this.seatMapper = seatMapper;
    }

    @Override
    public Page<SeatResponse> getSeatsByRoom(Integer roomId, String status, String seatType, Pageable pageable) {
        boolean hasStatus = status != null && !status.isEmpty();
        boolean hasType = seatType != null && !seatType.isEmpty();

        Page<Seat> seatPage;
        if (hasStatus && hasType) {
            seatPage = seatRepository.findByRoomIdAndStatusAndSeatType(roomId, status, seatType, pageable);
        } else if (hasStatus) {
            seatPage = seatRepository.findByRoomIdAndStatus(roomId, status, pageable);
        } else if (hasType) {
            seatPage = seatRepository.findByRoomIdAndSeatType(roomId, seatType, pageable);
        } else {
            seatPage = seatRepository.findByRoomId(roomId, pageable);
        }

        return seatPage.map(seatMapper::toResponse);
    }

    @Override
    @Transactional
    public List<SeatResponse> generateSeats(Integer roomId, GenerateSeatRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (seatRepository.countByRoomId(roomId) > 0) {
            throw new AppException(ErrorCode.ROOM_HAS_EXISTING_SEATS);
        }

        String seatType = request.getSeatType() != null
                ? request.getSeatType().toUpperCase()
                : "NORMAL";
        if (!SeatType.isValid(seatType)) {
            throw new AppException(ErrorCode.INVALID_SEAT_TYPE);
        }

        int rows = request.getRows();
        int seatsPerRow = request.getSeatsPerRow();

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            String rowLabel = toRowLabel(i);
            for (int j = 1; j <= seatsPerRow; j++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatRow(rowLabel);
                seat.setSeatNumber(j);
                seat.setSeatType(seatType);
                seat.setStatus("ACTIVE");
                seats.add(seat);
            }
        }

        List<Seat> savedSeats = seatRepository.saveAll(seats);
        return savedSeats.stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SeatResponse updateSeat(Integer roomId, Integer seatId, UpdateSeatRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NOT_FOUND));

        if (!seat.getRoom().getId().equals(roomId)) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }

        if (request.getSeatType() != null) {
            String seatType = request.getSeatType().toUpperCase();
            if (!SeatType.isValid(seatType)) {
                throw new AppException(ErrorCode.INVALID_SEAT_TYPE);
            }
            seat.setSeatType(seatType);
        }

        if (request.getStatus() != null) {
            String status = request.getStatus().toUpperCase();
            if (!SeatStatus.isValid(status)) {
                throw new AppException(ErrorCode.INVALID_SEAT_STATUS);
            }
            seat.setStatus(status);
        }

        Seat saved = seatRepository.save(seat);
        return seatMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<SeatResponse> batchUpdateSeats(Integer roomId, BatchUpdateSeatRequest request) {
        List<Integer> seatIds = request.getSeatIds();
        if (seatIds == null || seatIds.isEmpty()) {
            return List.of();
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(roomId)) {
                throw new AppException(ErrorCode.SEAT_NOT_FOUND);
            }
        }

        String seatType = request.getSeatType();
        if (seatType != null && !seatType.isEmpty()) {
            String upperType = seatType.toUpperCase();
            if (!SeatType.isValid(upperType)) {
                throw new AppException(ErrorCode.INVALID_SEAT_TYPE);
            }
            seats.forEach(s -> s.setSeatType(upperType));
        }

        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            String upperStatus = status.toUpperCase();
            if (!SeatStatus.isValid(upperStatus)) {
                throw new AppException(ErrorCode.INVALID_SEAT_STATUS);
            }
            seats.forEach(s -> s.setStatus(upperStatus));
        }

        List<Seat> savedSeats = seatRepository.saveAll(seats);
        return savedSeats.stream()
                .map(seatMapper::toResponse)
                .collect(Collectors.toList());
    }

    private String toRowLabel(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + index % 26));
            index = index / 26 - 1;
        }
        return sb.toString();
    }
}
