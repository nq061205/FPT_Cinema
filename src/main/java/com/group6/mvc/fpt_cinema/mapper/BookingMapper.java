package com.group6.mvc.fpt_cinema.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.CreateBookingResponse;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Ticket;

@Component
public class BookingMapper {

        public ViewBookingHistoryResponse toBookingHistoryResponse(Booking booking) {
                ViewBookingHistoryResponse response = new ViewBookingHistoryResponse();
                response.setBookingCode(booking.getBookingCode());
                response.setMovieTitle(booking.getShowtime().getMovie().getTitle());
                response.setRoomName(booking.getShowtime().getRoom().getRoomName());
                response.setStartTime(booking.getShowtime().getStartTime());
                response.setFinalAmount(booking.getFinalAmount());
                response.setStatus(booking.getStatus());
                response.setCreatedAt(booking.getCreatedAt());
                return response;
        }

        public CreateBookingResponse toCreateBookingResponse(Booking booking, List<Ticket> tickets) {
                List<CreateBookingTicketResponse> ticketResponses = tickets.stream()
                                .map(t -> new CreateBookingTicketResponse(
                                                t.getTicketCode(),
                                                t.getSeat().getSeatRow(),
                                                t.getSeat().getSeatNumber(),
                                                t.getSeat().getSeatType(),
                                                t.getPrice()))
                                .toList();

                CreateBookingResponse response = new CreateBookingResponse();
                response.setBookingCode(booking.getBookingCode());
                response.setMovieTitle(booking.getShowtime().getMovie().getTitle());
                response.setRoomName(booking.getShowtime().getRoom().getRoomName());
                response.setStartTime(booking.getShowtime().getStartTime());
                response.setSubtotal(booking.getSubtotal());
                response.setDiscountAmount(booking.getDiscountAmount());
                response.setFinalAmount(booking.getFinalAmount());
                response.setStatus(booking.getStatus());
                response.setExpiresAt(booking.getExpiresAt());
                response.setTickets(ticketResponses);
                return response;
        }
}
