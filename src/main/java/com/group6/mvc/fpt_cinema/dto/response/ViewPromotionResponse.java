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
public class ViewPromotionResponse {
    private Integer id;

    private String promotionCode;

    private String name;

    private String promotionType;

    private BigDecimal discountValue;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isActive = true;

    public ViewPromotionResponse(String name, String promotionType, BigDecimal discountValue, Boolean isActive) {
        this.name = name;
        this.promotionType = promotionType;
        this.discountValue = discountValue;
        this.isActive = isActive;
    }
}
