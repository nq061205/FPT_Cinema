package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;

import javax.swing.text.View;

import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.dto.request.SelectSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.mapper.SeatMapper;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.service.SeatService;

@Service
public class SeatServiceImpl
                extends AbstractCrudService<Seat, Integer>
                implements SeatService {

        private final SeatRepository seatRepository;
        private final SeatMapper seatMapper;
        private final TicketRepository ticketRepository;
        private final ShowtimeRepository showtimeRepository;

        public SeatServiceImpl(
                        SeatRepository repository,
                        SeatMapper mapper,
                        TicketRepository ticketRepository,
                        ShowtimeRepository showtimeRepository) {

                super(repository);

                this.seatRepository = repository;
                this.seatMapper = mapper;
                this.ticketRepository = ticketRepository;
                this.showtimeRepository = showtimeRepository;
        }

        @Override
        public ViewSeatMapResponse viewSeatMap(
                        ViewSeatMapRequest request) {

                Integer showtimeId = request.getShowtimeId();

                Showtime showtime = showtimeRepository
                                .findById(showtimeId)
                                .orElseThrow(() -> new RuntimeException("Showtime not found"));

                Integer roomId = showtime.getRoom().getId();

                List<Seat> seats = seatRepository.findByRoomId(roomId);

                List<ViewSeatResponse> seatResponses = seats.stream()
                                .map(seat -> {

                                        ViewSeatResponse dto = seatMapper.toViewSeatResponse(seat);

                                        boolean booked = ticketRepository
                                                        .existsBySeatIdAndShowtimeIdAndStatus(
                                                                        seat.getId(),
                                                                        showtimeId,
                                                                        "BOOKED");

                                        dto.setStatus(
                                                        booked
                                                                        ? "BOOKED"
                                                                        : "AVAILABLE");

                                        return dto;
                                })
                                .toList();

                ViewSeatMapResponse response = new ViewSeatMapResponse();

                response.setRoomId(roomId);

                response.setRoomName(
                                showtime.getRoom().getRoomName());

                response.setSeats(
                                seatResponses);

                return response;
        }

        @Override
        public ViewSeatResponse viewSeats(
                        ViewSeatRequest request) {

                Seat seat = seatRepository.findById(
                                request.getSeatId())
                                .orElseThrow(() -> new RuntimeException("Seat not found"));

                return seatMapper.toViewSeatResponse(seat);
        }
}