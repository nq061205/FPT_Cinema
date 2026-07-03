package com.group6.mvc.fpt_cinema.dto.payment.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Staff tạo payment tiền mặt cho một booking tại quầy vé.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCashPaymentRequest {

    private String bookingCode;
}
