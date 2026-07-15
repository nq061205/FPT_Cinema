package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ConfirmRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.ProcessPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.request.RejectRefundRequest;
import com.group6.mvc.fpt_cinema.dto.request.RequestRefundRequest;
import com.group6.mvc.fpt_cinema.dto.response.ConfirmRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.PendingRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.ProcessPaymentResponse;
import com.group6.mvc.fpt_cinema.dto.response.RejectRefundResponse;
import com.group6.mvc.fpt_cinema.dto.response.RequestRefundResponse;
import com.group6.mvc.fpt_cinema.security.SecurityUtils;
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
        response.setResult(paymentService.processPayment(SecurityUtils.getUserId(jwt), request));
        return response;
    }

    @PostMapping("/refund-request")
    public ApiResponse<RequestRefundResponse> requestRefund(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody RequestRefundRequest request) {

        ApiResponse<RequestRefundResponse> response = new ApiResponse<>();
        response.setMessage("Refund request submitted successfully!");
        response.setResult(paymentService.requestRefund(SecurityUtils.getUserId(jwt), request));
        return response;
    }

    @PostMapping("/refund-confirm")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ApiResponse<ConfirmRefundResponse> confirmRefund(
            @RequestBody ConfirmRefundRequest request) {

        ApiResponse<ConfirmRefundResponse> response = new ApiResponse<>();
        response.setMessage("Refund confirmed successfully!");
        response.setResult(paymentService.confirmRefund(request));
        return response;
    }

    @PostMapping("/refund-reject")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ApiResponse<RejectRefundResponse> rejectRefund(
            @RequestBody RejectRefundRequest request) {

        ApiResponse<RejectRefundResponse> response = new ApiResponse<>();
        response.setMessage("Refund rejected successfully!");
        response.setResult(paymentService.rejectRefund(request));
        return response;
    }

    @GetMapping("/refund-requests")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ApiResponse<List<PendingRefundResponse>> listPendingRefunds() {
        ApiResponse<List<PendingRefundResponse>> response = new ApiResponse<>();
        response.setMessage("Pending refunds retrieved successfully!");
        response.setResult(paymentService.listPendingRefunds());
        return response;
    }
}
