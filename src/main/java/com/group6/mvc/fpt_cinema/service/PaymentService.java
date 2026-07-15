package com.group6.mvc.fpt_cinema.service;

import java.util.List;
import java.util.Map;

import com.group6.mvc.fpt_cinema.dto.request.ConfirmRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RejectRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RequestRefundRequest;
import com.group6.mvc.fpt_cinema.dto.response.ConfirmRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.PendingRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.RejectRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.RequestRefundResponse;
import com.group6.mvc.fpt_cinema.dto.payment.request.ConfirmCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreateCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreatePaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.response.CreatePaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.PaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.VnpayReturnResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;

public interface PaymentService extends CrudService<Payment, Integer> {

    // ===== Từ origin/main: tạo & quản lý payment =====

    CreatePaymentResponse createOnlinePayment(Integer customerId, CreatePaymentRequest request, String clientIp);

    CreatePaymentResponse createCashPayment(CreateCashPaymentRequest request);

    PaymentResponse confirmCashPayment(ConfirmCashPaymentRequest request);

    VnpayReturnResponse handleVnpayReturn(Map<String, String> vnpParams);

    PaymentResponse getPaymentByCode(Integer customerId, String paymentCode);

    List<PaymentResponse> getPaymentHistory(Integer customerId);

    // ===== Từ HEAD: quản lý refund =====

    RequestRefundResponse requestRefund(Integer customerId, RequestRefundRequest request);

    ConfirmRefundResponse confirmRefund(ConfirmRefundRequest request);

    RejectRefundResponse rejectRefund(RejectRefundRequest request);

    List<PendingRefundResponse> listPendingRefunds();
}