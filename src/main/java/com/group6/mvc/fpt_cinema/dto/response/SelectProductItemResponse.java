package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectProductItemResponse {
    private Integer productId;
    private String name;
    private String productType;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
