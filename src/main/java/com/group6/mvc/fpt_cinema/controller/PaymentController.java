package com.group6.mvc.fpt_cinema.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.payment.request.ConfirmCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreateCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreatePaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.response.CreatePaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.PaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.VnpayReturnResponse;
import com.group6.mvc.fpt_cinema.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

        private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
                this.paymentService = paymentService;
        }

        // Customer thanh toán online (VNPAY) cho booking của chính mình
        @PostMapping("/create")
        public ApiResponse<CreatePaymentResponse> createOnlinePayment(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody CreatePaymentRequest request,
                        HttpServletRequest httpRequest) {

                Integer userId = Integer.valueOf(jwt.getClaimAsString("userId"));
                String forwarded = httpRequest.getHeader("X-Forwarded-For");
                String clientIp = (forwarded != null && !forwarded.isBlank())
                                ? forwarded.split(",")[0].trim()
                                : httpRequest.getRemoteAddr();

                CreatePaymentResponse result = paymentService.createOnlinePayment(userId, request, clientIp);

                return ApiResponse.<CreatePaymentResponse>builder()
                                .message("Payment initialized successfully!")
                                .result(result)
                                .build();
        }

        // Chỉ STAFF mới tạo được payment tiền mặt cho khách tại quầy
        @PreAuthorize("hasRole('STAFF')")
        @PostMapping("/cash")
        public ApiResponse<CreatePaymentResponse> createCashPayment(
                        @RequestBody CreateCashPaymentRequest request) {

                return ApiResponse.<CreatePaymentResponse>builder()
                                .message("Cash payment created successfully!")
                                .result(paymentService.createCashPayment(request))
                                .build();
        }

        // Chỉ nhân viên quầy vé (STAFF) mới được xác nhận thu tiền mặt
        @PreAuthorize("hasRole('STAFF')")
        @PostMapping("/cash/confirm")
        public ApiResponse<PaymentResponse> confirmCashPayment(
                        @RequestBody ConfirmCashPaymentRequest request) {

                return ApiResponse.<PaymentResponse>builder()
                                .message("Cash payment confirmed successfully!")
                                .result(paymentService.confirmCashPayment(request))
                                .build();
        }

        @GetMapping("/vnpay/return")
        public ApiResponse<VnpayReturnResponse> vnpayReturn(@RequestParam Map<String, String> params) {

                VnpayReturnResponse result = paymentService.handleVnpayReturn(params);
                return ApiResponse.<VnpayReturnResponse>builder()
                                .message(result.getMessage())
                                .result(result)
                                .build();
        }

        @GetMapping("/history")
        public ApiResponse<List<PaymentResponse>> getPaymentHistory(@AuthenticationPrincipal Jwt jwt) {

                return ApiResponse.<List<PaymentResponse>>builder()
                                .message("Payment history retrieved successfully!")
                                .result(paymentService
                                                .getPaymentHistory(Integer.valueOf(jwt.getClaimAsString("userId"))))
                                .build();
        }

        @GetMapping("/{paymentCode}")
        public ApiResponse<PaymentResponse> getPayment(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable String paymentCode) {

                return ApiResponse.<PaymentResponse>builder()
                                .message("Payment retrieved successfully!")
                                .result(paymentService.getPaymentByCode(Integer.valueOf(jwt.getClaimAsString("userId")),
                                                paymentCode))
                                .build();
        }
}
