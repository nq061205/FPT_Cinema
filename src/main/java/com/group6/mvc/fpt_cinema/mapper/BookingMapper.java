package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;

@Component
public class BookingMapper {
        public ViewBookingHistoryResponse toBookingHistoryResponse(
                        Booking booking) {

                ViewBookingHistoryResponse response = new ViewBookingHistoryResponse();

                response.setBookingCode(
                                booking.getBookingCode());

                response.setMovieTitle(
                                booking.getShowtime()
                                                .getMovie()
                                                .getTitle());

                response.setRoomName(
                                booking.getShowtime()
                                                .getRoom()
                                                .getRoomName());

                response.setStartTime(
                                booking.getShowtime()
                                                .getStartTime());

                response.setFinalAmount(
                                booking.getFinalAmount());

                response.setStatus(
                                booking.getStatus());

                response.setCreatedAt(
                                booking.getCreatedAt());

                return response;
        }
}
