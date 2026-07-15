package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.payment.response.CreatePaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.PaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.VnpayReturnResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;

@Component
public class PaymentMapper {

    public CreatePaymentResponse toCreatePaymentResponse(Payment payment, String paymentUrl) {
        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setPaymentCode(payment.getPaymentCode());
        response.setStatus(payment.getStatus());
        response.setMethod(payment.getMethod());
        response.setAmount(payment.getAmount());
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentCode(payment.getPaymentCode());
        response.setBookingCode(payment.getBooking().getBookingCode());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    public VnpayReturnResponse toVnpayReturnResponse(Payment payment, String responseCode, String message) {
        VnpayReturnResponse response = new VnpayReturnResponse();
        response.setPaymentCode(payment.getPaymentCode());
        response.setBookingCode(payment.getBooking().getBookingCode());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setResponseCode(responseCode);
        response.setMessage(message);
        return response;
    }
}
