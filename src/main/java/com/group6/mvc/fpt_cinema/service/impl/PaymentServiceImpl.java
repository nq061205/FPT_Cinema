package com.group6.mvc.fpt_cinema.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.group6.mvc.fpt_cinema.dto.payment.request.ConfirmCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreateCashPaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.request.CreatePaymentRequest;
import com.group6.mvc.fpt_cinema.dto.payment.response.CreatePaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.PaymentResponse;
import com.group6.mvc.fpt_cinema.dto.payment.response.VnpayReturnResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.enums.BookingStatus;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.PaymentMethod;
import com.group6.mvc.fpt_cinema.enums.PaymentStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.integration.vnpay.VnPayProperties;
import com.group6.mvc.fpt_cinema.integration.vnpay.VnPayUtil;
import com.group6.mvc.fpt_cinema.mapper.PaymentMapper;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.service.PaymentService;

@Service
public class PaymentServiceImpl
        extends AbstractCrudService<Payment, Integer>
        implements PaymentService {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final VnPayProperties vnPayProperties;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            PaymentMapper paymentMapper,
            VnPayProperties vnPayProperties) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.vnPayProperties = vnPayProperties;
    }

    // Customer thanh toán online (chỉ VNPAY) cho booking của chính mình
    @Override
    @Transactional
    public CreatePaymentResponse createOnlinePayment(Integer customerId, CreatePaymentRequest request,
            String clientIp) {
        if (request == null || !StringUtils.hasText(request.getBookingCode())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        // Customer chỉ được thanh toán online; tiền mặt do STAFF xử lý tại quầy
        if (request.getMethod() != PaymentMethod.VNPAY) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        Booking booking = bookingRepository.findByBookingCodeAndCustomerId(request.getBookingCode(), customerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_NOT_PAYABLE);
        }
        if (paymentRepository.existsByBookingIdAndStatus(booking.getId(), PaymentStatus.PAID)) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        // Tạo mã thanh toán: PAY + thời gian + 4 số ngẫu nhiên
        String paymentCode = "PAY"
                + ZonedDateTime.now(VN_ZONE).format(VNP_DATE_FORMAT)
                + ThreadLocalRandom.current().nextInt(1000, 10000);

        // Nếu booking đã có payment PENDING thì tái dùng, không tạo row mới
        Payment payment = paymentRepository
                .findByBookingIdAndStatus(booking.getId(), PaymentStatus.PENDING)
                .orElseGet(Payment::new);
        payment.setBooking(booking);
        payment.setPaymentCode(paymentCode);
        payment.setMethod(PaymentMethod.VNPAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(booking.getFinalAmount());
        payment.setRefundAmount(BigDecimal.ZERO);
        payment = paymentRepository.save(payment);

        // Dựng link thanh toán để redirect khách sang cổng VNPay
        // VNPay yêu cầu số tiền nhân 100 và không có phần thập phân
        BigDecimal amount = booking.getFinalAmount().multiply(BigDecimal.valueOf(100));
        ZonedDateTime now = ZonedDateTime.now(VN_ZONE);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", vnPayProperties.getVersion());
        params.put("vnp_Command", vnPayProperties.getCommand());
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        params.put("vnp_Amount", amount.toBigInteger().toString());
        params.put("vnp_CurrCode", vnPayProperties.getCurrencyCode());
        params.put("vnp_TxnRef", paymentCode);
        params.put("vnp_OrderInfo", "Thanh toan booking " + booking.getBookingCode());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", vnPayProperties.getLocale());
        params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        params.put("vnp_IpAddr", StringUtils.hasText(clientIp) ? clientIp : "127.0.0.1");
        params.put("vnp_CreateDate", now.format(VNP_DATE_FORMAT));
        params.put("vnp_ExpireDate",
                now.plusMinutes(vnPayProperties.getPaymentTimeoutMinutes()).format(VNP_DATE_FORMAT));
        if (StringUtils.hasText(request.getBankCode())) {
            params.put("vnp_BankCode", request.getBankCode());
        }

        String query = VnPayUtil.buildQueryWithHash(params, vnPayProperties.getHashSecret());
        String paymentUrl = vnPayProperties.getPayUrl() + "?" + query;
        return paymentMapper.toCreatePaymentResponse(payment, paymentUrl);
    }

    @Override
    @Transactional
    public CreatePaymentResponse createCashPayment(CreateCashPaymentRequest request) {
        if (request == null || !StringUtils.hasText(request.getBookingCode())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Staff thao tác cho khách nên tra booking theo bookingCode, không gắn customer
        // đăng nhập
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_NOT_PAYABLE);
        }
        if (paymentRepository.existsByBookingIdAndStatus(booking.getId(), PaymentStatus.PAID)) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        String paymentCode = "PAY"
                + ZonedDateTime.now(VN_ZONE).format(VNP_DATE_FORMAT)
                + ThreadLocalRandom.current().nextInt(1000, 10000);

        // Nếu booking đã có payment PENDING thì tái dùng, không tạo row mới
        Payment payment = paymentRepository
                .findByBookingIdAndStatus(booking.getId(), PaymentStatus.PENDING)
                .orElseGet(Payment::new);
        payment.setBooking(booking);
        payment.setPaymentCode(paymentCode);
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(booking.getFinalAmount());
        payment.setRefundAmount(BigDecimal.ZERO);
        payment = paymentRepository.save(payment);

        return paymentMapper.toCreatePaymentResponse(payment, null);
    }

    @Override
    @Transactional
    public PaymentResponse confirmCashPayment(ConfirmCashPaymentRequest request) {
        if (request == null || !StringUtils.hasText(request.getPaymentCode())) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Payment payment = paymentRepository.findByPaymentCode(request.getPaymentCode())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getMethod() != PaymentMethod.CASH) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_MISMATCH);
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new AppException(ErrorCode.PAYMENT_NOT_PENDING);
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public VnpayReturnResponse handleVnpayReturn(Map<String, String> vnpParams) {
        if (vnpParams == null || vnpParams.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        if (!VnPayUtil.verifySignature(vnpParams, vnPayProperties.getHashSecret())) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_SIGNATURE);
        }

        Payment payment = paymentRepository.findByPaymentCode(vnpParams.get("vnp_TxnRef"))
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return paymentMapper.toVnpayReturnResponse(payment, "00", "Payment already confirmed");
        }
        if (payment.getMethod() != PaymentMethod.VNPAY) {
            throw new AppException(ErrorCode.PAYMENT_METHOD_MISMATCH);
        }

        String responseCode = vnpParams.get("vnp_ResponseCode");
        BigDecimal expectedAmount = payment.getAmount().multiply(BigDecimal.valueOf(100));
        BigDecimal returnedAmount = new BigDecimal(vnpParams.get("vnp_Amount"));
        boolean amountMatches = expectedAmount.compareTo(returnedAmount) == 0;
        boolean success = "00".equals(responseCode) && "00".equals(vnpParams.get("vnp_TransactionStatus"));

        if (success && amountMatches) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return paymentMapper.toVnpayReturnResponse(payment, "00", "Payment successful");
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        String message = amountMatches
                ? "Payment failed (VNPay response code " + responseCode + ")"
                : "Amount mismatch";
        return paymentMapper.toVnpayReturnResponse(payment, responseCode, message);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByCode(Integer customerId, String paymentCode) {
        Payment payment = paymentRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getBooking().getCustomer().getId().equals(customerId)) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentHistory(Integer customerId) {
        return paymentRepository.findByBookingCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();
    }
}
