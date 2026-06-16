package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.mapper.SeatMapper;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.service.SeatService;

@Service
public class SeatServiceImpl
        extends AbstractCrudService<Seat, Integer>
        implements SeatService {

    private SeatRepository seatRepository;
    private SeatMapper seatMapper;

    public SeatServiceImpl(SeatRepository repository, SeatMapper mapper) {
        super(repository);
        this.seatRepository = repository;
        this.seatMapper = mapper;
    }

    @Override
    public ViewSeatMapResponse viewSeatListByRoom(
            Integer roomId,
            ViewSeatMapRequest request) {

        List<Seat> seats = seatRepository.findByRoomId(roomId);

        List<ViewSeatResponse> seatResponses = seats.stream()
                .map(seatMapper::toViewSeatResponse)
                .toList();

        ViewSeatMapResponse response = new ViewSeatMapResponse();

        response.setRoomId(roomId);

        if (!seats.isEmpty()) {
            response.setRoomName(
                    seats.get(0)
                            .getRoom()
                            .getRoomName());
        }

        response.setSeats(seatResponses);

        return response;
    }
}
