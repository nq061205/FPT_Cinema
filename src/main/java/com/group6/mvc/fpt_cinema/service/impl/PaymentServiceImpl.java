package com.group6.mvc.fpt_cinema.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.service.PaymentService;

@Service
public class PaymentServiceImpl
        extends AbstractCrudService<Payment, Integer>
        implements PaymentService {

    private static final Set<String> VALID_METHODS = Set.of("CASH", "VNPAY");

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            TicketRepository ticketRepository) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public ProcessPaymentResponse processPayment(Integer customerId, ProcessPaymentRequest request) {
        String methodStr = request.getMethod() != null
                ? request.getMethod().toUpperCase().trim()
                : null;
        if (methodStr == null || !VALID_METHODS.contains(methodStr)) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        Booking booking = bookingRepository.findByIdAndCustomerId(request.getBookingId(), customerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_PAID);
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_NOT_PENDING);
        }
        if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new AppException(ErrorCode.BOOKING_EXPIRED);
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentCode("PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        payment.setMethod(PaymentMethod.valueOf(methodStr));
        payment.setAmount(booking.getFinalAmount());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(now);
        Payment savedPayment = paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        tickets.forEach(t -> t.setStatus("BOOKED"));
        ticketRepository.saveAll(tickets);

        ProcessPaymentResponse response = new ProcessPaymentResponse();
        response.setPaymentCode(savedPayment.getPaymentCode());
        response.setBookingCode(booking.getBookingCode());
        response.setAmount(savedPayment.getAmount());
        response.setMethod(savedPayment.getMethod());
        response.setStatus(savedPayment.getStatus());
        response.setPaidAt(savedPayment.getPaidAt());
        return response;
    }
}
