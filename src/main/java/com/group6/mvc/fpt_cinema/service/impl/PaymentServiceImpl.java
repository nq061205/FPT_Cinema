package com.group6.mvc.fpt_cinema.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.ConfirmRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.request.RejectRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RequestRefundRequest;
import com.group6.mvc.fpt_cinema.dto.response.ConfirmRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.PendingRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentProductResponse;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.dto.response.RejectRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.RequestRefundResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.BookingProduct;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.entity.Promotion;
import com.group6.mvc.fpt_cinema.entity.Ticket;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
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
import com.group6.mvc.fpt_cinema.service.PaymentService;

@Service
public class PaymentServiceImpl
        extends AbstractCrudService<Payment, Integer>
        implements PaymentService {

    private static final Set<String> VALID_METHODS = Set.of("CASH", "VNPAY");

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final BookingProductRepository bookingProductRepository;
    private final UserPromotionRepository userPromotionRepository;
    private final PromotionRepository promotionRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            BookingProductRepository bookingProductRepository,
            UserPromotionRepository userPromotionRepository,
            PromotionRepository promotionRepository) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.bookingProductRepository = bookingProductRepository;
        this.userPromotionRepository = userPromotionRepository;
        this.promotionRepository = promotionRepository;
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

            // Revert tickets to CANCELLED (release seats)
            List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
            tickets.forEach(t -> t.setStatus("CANCELLED"));
            ticketRepository.saveAll(tickets);

            // Revert promotion to AVAILABLE (clear usedAt)
            if (booking.getPromotion() != null) {
                userPromotionRepository.findByUserIdAndPromotionId(customerId, booking.getPromotion().getId())
                        .ifPresent(up -> {
                            up.setStatus(UserPromotionStatus.AVAILABLE);
                            up.setUsedAt(null);
                            userPromotionRepository.save(up);
                        });
            }

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

        List<BookingProduct> bookingProducts = bookingProductRepository.findByBookingId(booking.getId());

        ProcessPaymentResponse response = new ProcessPaymentResponse();
        response.setPaymentCode(savedPayment.getPaymentCode());
        response.setBookingCode(booking.getBookingCode());
        response.setAmount(savedPayment.getAmount());
        response.setMethod(savedPayment.getMethod());
        response.setStatus(savedPayment.getStatus());
        response.setPaidAt(savedPayment.getPaidAt());

        // Rich invoice info
        response.setMovieTitle(booking.getShowtime().getMovie().getTitle());
        response.setRoomName(booking.getShowtime().getRoom().getRoomName());
        response.setStartTime(booking.getShowtime().getStartTime());
        response.setSubtotal(booking.getSubtotal());
        response.setDiscountAmount(booking.getDiscountAmount());
        response.setFinalAmount(booking.getFinalAmount());

        if (booking.getPromotion() != null) {
            response.setPromotionCode(booking.getPromotion().getPromotionCode());
            response.setPromotionName(booking.getPromotion().getName());
        }

        // Map tickets
        List<CreateBookingTicketResponse> ticketResponses = tickets.stream()
                .map(t -> new CreateBookingTicketResponse(
                        t.getTicketCode(),
                        t.getSeat().getSeatRow(),
                        t.getSeat().getSeatNumber(),
                        t.getSeat().getSeatType(),
                        t.getPrice()
                ))
                .toList();
        response.setTickets(ticketResponses);

        // Map products
        List<ProcessPaymentProductResponse> productResponses = bookingProducts.stream()
                .map(bp -> new ProcessPaymentProductResponse(
                        bp.getProduct().getName(),
                        bp.getQuantity(),
                        bp.getUnitPrice(),
                        bp.getTotalPrice()
                ))
                .toList();
        response.setProducts(productResponses);

        return response;
    }

    @Override
    @Transactional
    public RequestRefundResponse requestRefund(Integer customerId, RequestRefundRequest request) {
        if (request == null || request.getBookingId() == null || request.getMethod() == null || request.getMethod().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Booking booking = bookingRepository.findByIdAndCustomerId(request.getBookingId(), customerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new AppException(ErrorCode.BOOKING_NOT_COMPLETED);
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = paymentRepository.findFirstByBookingIdOrderByIdDesc(booking.getId())
                .orElseGet(() -> createMissingPayment(booking, now));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new AppException(ErrorCode.REFUND_ALREADY_COMPLETED);
        }
        if (Boolean.TRUE.equals(payment.getRefundRequested())) {
            throw new AppException(ErrorCode.REFUND_ALREADY_REQUESTED);
        }

        RefundMethod refundMethod;
        try {
            refundMethod = RefundMethod.valueOf(request.getMethod() != null ? request.getMethod().toUpperCase().trim() : "");
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_REFUND_METHOD);
        }

        payment.setRefundRequested(true);
        payment.setRefundMethod(refundMethod);
        payment.setRefundRequestedAt(now);
        paymentRepository.save(payment);

        return RequestRefundResponse.builder()
                .bookingCode(booking.getBookingCode())
                .refundMethod(refundMethod)
                .refundRequestedAt(now)
                .build();
    }

    private Payment createMissingPayment(Booking booking, LocalDateTime now) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentCode("PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        payment.setMethod(PaymentMethod.CASH);
        payment.setAmount(booking.getFinalAmount());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(now);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public ConfirmRefundResponse confirmRefund(ConfirmRefundRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findFirstByBookingIdOrderByIdDesc(booking.getId())
                .orElseGet(() -> createMissingPayment(booking, LocalDateTime.now()));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new AppException(ErrorCode.REFUND_ALREADY_COMPLETED);
        }
        if (!Boolean.TRUE.equals(payment.getRefundRequested())) {
            throw new AppException(ErrorCode.REFUND_NOT_REQUESTED);
        }

        LocalDateTime now = LocalDateTime.now();
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundedAt(now);
        paymentRepository.save(payment);

        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        tickets.forEach(t -> t.setStatus("REFUNDED"));
        ticketRepository.saveAll(tickets);

        String voucherCode = null;
        if (payment.getRefundMethod() == RefundMethod.ONLINE) {
            voucherCode = createRefundVoucher(booking, payment, now);
            payment.setRefundVoucherCode(voucherCode);
            paymentRepository.save(payment);
        }

        return ConfirmRefundResponse.builder()
                .bookingCode(booking.getBookingCode())
                .refundMethod(payment.getRefundMethod())
                .refundAmount(payment.getRefundAmount())
                .refundedAt(now)
                .voucherCode(voucherCode)
                .build();
    }

    private String createRefundVoucher(Booking booking, Payment payment, LocalDateTime now) {
        Promotion promotion = new Promotion();
        promotion.setPromotionCode("REFUND-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        promotion.setName("Refund Voucher for " + booking.getBookingCode());
        promotion.setPromotionType("FIXED_AMOUNT");
        promotion.setDiscountValue(payment.getRefundAmount().max(BigDecimal.ZERO));
        promotion.setStartDate(now);
        promotion.setEndDate(now.plusDays(30));
        promotion.setIsActive(true);
        promotion = promotionRepository.save(promotion);

        User_Promotion userPromotion = new User_Promotion();
        userPromotion.setUser(booking.getCustomer());
        userPromotion.setPromotion(promotion);
        userPromotion.setStatus(UserPromotionStatus.AVAILABLE);
        userPromotion.setAssignedAt(now);
        userPromotionRepository.save(userPromotion);

        return promotion.getPromotionCode();
    }

    @Override
    @Transactional
    public RejectRefundResponse rejectRefund(RejectRefundRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findFirstByBookingIdOrderByIdDesc(booking.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new AppException(ErrorCode.REFUND_ALREADY_COMPLETED);
        }
        if (!Boolean.TRUE.equals(payment.getRefundRequested())) {
            throw new AppException(ErrorCode.REFUND_NOT_REQUESTED);
        }

        LocalDateTime now = LocalDateTime.now();
        payment.setRefundRequested(false);
        payment.setRefundRejectedAt(now);
        payment.setRefundRejectionReason(request.getReason());
        paymentRepository.save(payment);

        return RejectRefundResponse.builder()
                .bookingCode(booking.getBookingCode())
                .reason(request.getReason())
                .rejectedAt(now)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingRefundResponse> listPendingRefunds() {
        List<Payment> payments = paymentRepository
                .findByRefundRequestedTrueAndStatusNotOrderByRefundRequestedAtAsc(PaymentStatus.REFUNDED);

        return payments.stream()
                .map(payment -> {
                    Booking booking = payment.getBooking();
                    return PendingRefundResponse.builder()
                            .bookingId(booking.getId())
                            .bookingCode(booking.getBookingCode())
                            .movieTitle(booking.getShowtime().getMovie().getTitle())
                            .customerName(booking.getCustomer().getFullName())
                            .customerPhone(booking.getCustomer().getPhone())
                            .finalAmount(booking.getFinalAmount())
                            .refundMethod(payment.getRefundMethod())
                            .refundRequestedAt(payment.getRefundRequestedAt())
                            .build();
                })
                .toList();
    }
}
