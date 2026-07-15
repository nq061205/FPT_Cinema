package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

import com.group6.mvc.fpt_cinema.enums.RefundMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestRefundResponse {
    private String bookingCode;
    private RefundMethod refundMethod;
    private LocalDateTime refundRequestedAt;
}
