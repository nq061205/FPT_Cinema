package com.group6.mvc.fpt_cinema.service;

import java.util.List;
import java.util.Map;

import com.group6.mvc.fpt_cinema.dto.payment.request.ConfirmCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreateCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreatePaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.response.CreatePaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.PaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.VnpayReturnResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;

public interface PaymentService extends CrudService<Payment, Integer> {

    CreatePaymentResponse createOnlinePayment(Integer customerId, CreatePaymentRequest request, String clientIp);

    CreatePaymentResponse createCashPayment(CreateCashPaymentRequest request);

    PaymentResponse confirmCashPayment(ConfirmCashPaymentRequest request);

    VnpayReturnResponse handleVnpayReturn(Map<String, String> vnpParams);

    PaymentResponse getPaymentByCode(Integer customerId, String paymentCode);

    List<PaymentResponse> getPaymentHistory(Integer customerId);
}
