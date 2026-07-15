package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

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
public class RejectRefundResponse {
    private String bookingCode;
    private String reason;
    private LocalDateTime rejectedAt;
}
