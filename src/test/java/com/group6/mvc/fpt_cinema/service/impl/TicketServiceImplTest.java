package com.group6.mvc.fpt_cinema.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group6.mvc.fpt_cinema.dto.request.CheckInTicketRequest;
import com.group6.mvc.fpt_cinema.dto.response.CheckInTicketResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.entity.Room;
import com.group6.mvc.fpt_cinema.entity.Seat;
import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void checkIn_shouldMarkBookingCompleted_whenAllTicketsInBookingAreUsed() {
        Booking booking = new Booking();
        booking.setId(10);
        booking.setStatus(BookingStatus.CONFIRMED);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setBooking(booking);
        ticket.setStatus("BOOKED");
        ticket.setTicketCode("TICKET-001");

        Movie movie = new Movie();
        movie.setTitle("Interstellar");

        Room room = new Room();
        room.setRoomName("Room A");

        Seat seat = new Seat();
        seat.setSeatRow("A");
        seat.setSeatNumber(1);

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(LocalDateTime.of(2026, 7, 15, 20, 0));

        ticket.setShowtime(showtime);
        ticket.setSeat(seat);

        when(ticketRepository.findByTicketCode("TICKET-001")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.findByBookingId(10)).thenReturn(List.of(ticket));

        CheckInTicketResponse response = ticketService.checkIn(new CheckInTicketRequest("TICKET-001"));

        assertEquals("USED", response.getStatus());
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }
}
