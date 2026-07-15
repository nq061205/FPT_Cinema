package com.group6.mvc.fpt_cinema.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.CreateBookingResponse;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentProductResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.BookingProduct;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.repository.BookingProductRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;

@Component
public class BookingMapper {

        private final TicketRepository ticketRepository;
        private final BookingProductRepository bookingProductRepository;
        private final PaymentRepository paymentRepository;

        public BookingMapper(
                        TicketRepository ticketRepository,
                        BookingProductRepository bookingProductRepository,
                        PaymentRepository paymentRepository) {
                this.ticketRepository = ticketRepository;
                this.bookingProductRepository = bookingProductRepository;
                this.paymentRepository = paymentRepository;
        }

        public ViewBookingHistoryResponse toBookingHistoryResponse(Booking booking) {
                ViewBookingHistoryResponse response = new ViewBookingHistoryResponse();
                response.setId(booking.getId());
                response.setBookingCode(booking.getBookingCode());
                response.setMovieTitle(booking.getShowtime().getMovie().getTitle());
                response.setRoomName(booking.getShowtime().getRoom().getRoomName());
                response.setStartTime(booking.getShowtime().getStartTime());
                response.setSubtotal(booking.getSubtotal());
                response.setDiscountAmount(booking.getDiscountAmount());
                response.setFinalAmount(booking.getFinalAmount());
                response.setStatus(booking.getStatus());
                response.setCreatedAt(booking.getCreatedAt());

                if (booking.getPromotion() != null) {
                        response.setPromotionCode(booking.getPromotion().getPromotionCode());
                        response.setPromotionName(booking.getPromotion().getName());
                }

                paymentRepository.findFirstByBookingIdOrderByIdDesc(booking.getId())
                                .ifPresent(payment -> {
                                        response.setMethod(payment.getMethod());
                                        response.setRefundRequested(Boolean.TRUE.equals(payment.getRefundRequested()));
                                        response.setRefundMethod(payment.getRefundMethod());
                                        response.setRefundCompleted(payment.getStatus() == PaymentStatus.REFUNDED);
                                        response.setRefundedAt(payment.getRefundedAt());
                                        response.setRefundVoucherCode(payment.getRefundVoucherCode());
                                });

                List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
                response.setTickets(tickets.stream()
                                .map(t -> new CreateBookingTicketResponse(
                                                t.getTicketCode(),
                                                t.getSeat().getSeatRow(),
                                                t.getSeat().getSeatNumber(),
                                                t.getSeat().getSeatType(),
                                                t.getPrice()))
                                .toList());

                List<BookingProduct> bookingProducts = bookingProductRepository.findByBookingId(booking.getId());
                response.setProducts(bookingProducts.stream()
                                .map(bp -> new ProcessPaymentProductResponse(
                                                bp.getProduct().getName(),
                                                bp.getQuantity(),
                                                bp.getUnitPrice(),
                                                bp.getTotalPrice()))
                                .toList());

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
                response.setId(booking.getId());
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
