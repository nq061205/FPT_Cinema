package com.group6.mvc.fpt_cinema.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group6.mvc.fpt_cinema.dto.request.ConfirmRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RequestRefundRequest;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.enums.RefundMethod;
import com.group6.mvc.fpt_cinema.enums.UserPromotionStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.BookingProductRepository;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.repository.PromotionRepository;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.repository.UserPromotionRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private BookingProductRepository bookingProductRepository;

    @Mock
    private UserPromotionRepository userPromotionRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void requestRefund_shouldThrowInvalidInput_whenRequestIsMissing() {
        AppException exception = assertThrows(AppException.class,
                () -> paymentService.requestRefund(1, null));

        assertThrows(AppException.class, () -> paymentService.requestRefund(1, null));
        assertThrows(AppException.class, () -> paymentService.requestRefund(1, new RequestRefundRequest(null, "CASH")));
        assert exception.getErrorCode() == ErrorCode.INVALID_INPUT;
    }

    @Test
    void confirmRefund_shouldCreateVoucherForOnlineRefund() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookingCode("BK123");
        User customer = new User();
        customer.setId(10);
        booking.setCustomer(customer);

        Payment payment = new Payment();
        payment.setId(1);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setRefundRequested(true);
        payment.setRefundMethod(RefundMethod.ONLINE);
        payment.setStatus(PaymentStatus.COMPLETED);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(paymentRepository.findFirstByBookingIdOrderByIdDesc(1)).thenReturn(Optional.of(payment));
        when(ticketRepository.findByBookingId(1)).thenReturn(List.of());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userPromotionRepository.save(any(User_Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.confirmRefund(new ConfirmRefundRequest(1));

        verify(promotionRepository).save(any(Promotion.class));
        verify(userPromotionRepository).save(any(User_Promotion.class));
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
    }
}
