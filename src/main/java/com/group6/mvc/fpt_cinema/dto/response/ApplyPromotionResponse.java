package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;

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
public class ApplyPromotionResponse {

    private String name;
    private String promotionType;
    private BigDecimal discountValue;
}
