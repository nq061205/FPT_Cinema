package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ApiResponse<ProcessPaymentResponse> processPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ProcessPaymentRequest request) {

        ApiResponse<ProcessPaymentResponse> response = new ApiResponse<>();
        response.setMessage("Payment processed successfully!");
        response.setResult(paymentService.processPayment(getIntegerClaim(jwt, "userId"), request));
        return response;
    }

    private Integer getIntegerClaim(Jwt jwt, String claimName) {
        Number value = jwt.getClaim(claimName);
        return value == null ? null : value.intValue();
    }
}
