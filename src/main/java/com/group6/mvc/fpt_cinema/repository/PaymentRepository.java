package com.group6.mvc.fpt_cinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findFirstByBookingIdOrderByIdDesc(Integer bookingId);

    List<Payment> findByRefundRequestedTrueAndStatusNotOrderByRefundRequestedAtAsc(PaymentStatus status);

    Optional<Payment> findByPaymentCode(String paymentCode);

    boolean existsByBookingIdAndStatus(Integer bookingId, PaymentStatus status);

    Optional<Payment> findByBookingIdAndStatus(Integer bookingId, PaymentStatus status);

    List<Payment> findByBookingCustomerIdOrderByCreatedAtDesc(Integer customerId);
}