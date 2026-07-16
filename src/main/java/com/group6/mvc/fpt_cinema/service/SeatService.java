package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group6.mvc.fpt_cinema.dto.request.BatchUpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.GenerateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateSeatRequest;
import com.group6.mvc.fpt_cinema.dto.response.SeatResponse;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewSeatRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;

public interface SeatService extends CrudService<Seat, Integer> {

    Page<SeatResponse> getSeatsByRoom(Integer roomId, String status, String seatType, Pageable pageable);

    List<SeatResponse> generateSeats(Integer roomId, GenerateSeatRequest request);

    SeatResponse updateSeat(Integer roomId, Integer seatId, UpdateSeatRequest request);

    List<SeatResponse> batchUpdateSeats(Integer roomId, BatchUpdateSeatRequest request);

    void deleteSeat(Integer roomId, Integer seatId);

    void batchDeleteSeats(Integer roomId, List<Integer> seatIds);

        ViewSeatMapResponse viewSeatMap(
                        ViewSeatMapRequest request);

        ViewSeatResponse viewSeats(
                        ViewSeatRequest request);
}
