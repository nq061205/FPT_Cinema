package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.entity.Payment;

public interface PaymentService extends CrudService<Payment, Integer> {

    ProcessPaymentResponse processPayment(Integer customerId, ProcessPaymentRequest request);
}
