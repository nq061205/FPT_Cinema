package com.group6.mvc.fpt_cinema.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductResponse {
    private Integer id;
    private String name;
    private String productType;
    private String imageUrl;
    private BigDecimal price;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}
