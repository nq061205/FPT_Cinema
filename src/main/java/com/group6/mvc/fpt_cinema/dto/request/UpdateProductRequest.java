package com.group6.mvc.fpt_cinema.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String name;
    private String productType;
    private String imageUrl;
    private BigDecimal price;
    private Boolean isActive;
}
