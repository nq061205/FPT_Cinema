package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.ViewSeatResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;

@Component
public class SeatMapper {
        public ViewSeatResponse toViewSeatResponse(Seat seat) {
                ViewSeatResponse response = new ViewSeatResponse();
                response.setId(seat.getId());
                response.setSeatNumber(seat.getSeatNumber());
                response.setSeatRow(seat.getSeatRow());
                response.setSeatType(seat.getSeatType());
                response.setStatus(seat.getStatus());
                return response;
        }


        public ViewSeatResponse toViewSeatResponse(
                        Seat seat,
                        boolean isSelected) {

                ViewSeatResponse response = new ViewSeatResponse();

                response.setId(seat.getId());

                response.setSeatNumber(
                                seat.getSeatNumber());

                response.setSeatRow(
                                seat.getSeatRow());

                response.setSeatType(
                                seat.getSeatType());

                response.setStatus(
                                isSelected ? "SELECTED" : seat.getStatus());

                return response;
        }
}
