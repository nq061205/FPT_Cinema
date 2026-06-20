package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectProductResponse {
    private List<SelectProductItemResponse> items;
    private BigDecimal totalAmount;
}
