package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.ConfirmRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.request.RejectRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RequestRefundRequest;
import com.group6.mvc.fpt_cinema.dto.response.ConfirmRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.PendingRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.dto.response.RejectRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.RequestRefundResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;

public interface PaymentService extends CrudService<Payment, Integer> {

    ProcessPaymentResponse processPayment(Integer customerId, ProcessPaymentRequest request);

    RequestRefundResponse requestRefund(Integer customerId, RequestRefundRequest request);

    ConfirmRefundResponse confirmRefund(ConfirmRefundRequest request);

    RejectRefundResponse rejectRefund(RejectRefundRequest request);

    List<PendingRefundResponse> listPendingRefunds();
}
