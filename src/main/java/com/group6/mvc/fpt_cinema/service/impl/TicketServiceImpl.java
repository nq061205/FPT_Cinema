package com.group6.mvc.fpt_cinema.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.CheckInTicketRequest;
import com.group6.mvc.fpt_cinema.dto.request.TicketLookupRequest;
import com.group6.mvc.fpt_cinema.dto.response.CheckInTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.TicketLookupResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.service.TicketService;

@Service
public class TicketServiceImpl
        extends AbstractCrudService<Ticket, Integer>
        implements TicketService {

    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public TicketServiceImpl(TicketRepository repository, PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        super(repository);
        this.ticketRepository = repository;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public CheckInTicketResponse checkIn(CheckInTicketRequest request) {
        if (request == null || request.getTicketCode() == null || request.getTicketCode().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Ticket ticket = ticketRepository.findByTicketCode(request.getTicketCode().trim())
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));

        String status = ticket.getStatus();
        if ("USED".equals(status) || "CHECKED_IN".equals(status)) {
            throw new AppException(ErrorCode.TICKET_ALREADY_USED);
        }
        if (!"BOOKED".equals(status)) {
            throw new AppException(ErrorCode.TICKET_INVALID_STATUS);
        }

        LocalDateTime now = LocalDateTime.now();
        autoCompleteBookingIfShowtimePassed(ticket.getBooking(), now);

        if (ticket.getBooking().getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.TICKET_BOOKING_NOT_CONFIRMED);
        }
        ticket.setStatus("USED");
        ticket.setCheckedInAt(now);
        ticketRepository.save(ticket);

        if (isBookingFullyUsed(ticket.getBooking().getId())) {
            ticket.getBooking().setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(ticket.getBooking());
        }

        return CheckInTicketResponse.builder()
                .ticketCode(ticket.getTicketCode())
                .movieTitle(ticket.getShowtime().getMovie().getTitle())
                .roomName(ticket.getShowtime().getRoom().getRoomName())
                .seatRow(ticket.getSeat().getSeatRow())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .startTime(ticket.getShowtime().getStartTime())
                .checkedInAt(now)
                .status("USED")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketLookupResponse lookup(TicketLookupRequest request) {
        if (request == null || request.getTicketCode() == null || request.getTicketCode().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Ticket ticket = ticketRepository.findByTicketCode(request.getTicketCode().trim())
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));

        autoCompleteBookingIfShowtimePassed(ticket.getBooking(), LocalDateTime.now());

        Payment payment = paymentRepository.findFirstByBookingIdOrderByIdDesc(ticket.getBooking().getId())
                .orElse(null);

        TicketLookupResponse.TicketLookupResponseBuilder builder = TicketLookupResponse.builder()
                .ticketCode(ticket.getTicketCode())
                .movieTitle(ticket.getShowtime().getMovie().getTitle())
                .roomName(ticket.getShowtime().getRoom().getRoomName())
                .seatRow(ticket.getSeat().getSeatRow())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .startTime(ticket.getShowtime().getStartTime())
                .status(ticket.getStatus())
                .checkedInAt(ticket.getCheckedInAt())
                .bookingId(ticket.getBooking().getId())
                .bookingCode(ticket.getBooking().getBookingCode())
                .finalAmount(ticket.getBooking().getFinalAmount());

        if (payment != null) {
            builder.refundRequested(Boolean.TRUE.equals(payment.getRefundRequested()))
                    .refundMethod(payment.getRefundMethod())
                    .refundCompleted(payment.getStatus() == PaymentStatus.REFUNDED)
                    .refundedAt(payment.getRefundedAt());
        } else {
            builder.refundRequested(false).refundCompleted(false);
        }

        return builder.build();
    }

    private void autoCompleteBookingIfShowtimePassed(com.group6.mvc.fpt_cinema.entity.Booking booking, LocalDateTime now) {
        if (booking == null || booking.getShowtime() == null || booking.getShowtime().getStartTime() == null) {
            return;
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED && !now.isBefore(booking.getShowtime().getStartTime())) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }
    }

    private boolean isBookingFullyUsed(Integer bookingId) {
        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        return !tickets.isEmpty() && tickets.stream().allMatch(t -> "USED".equals(t.getStatus()));
    }
}
