package com.group6.mvc.fpt_cinema.dto.report.request;

import com.group6.mvc.fpt_cinema.enums.PromotionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionReportRequest extends ReportFilterRequest {
    private String promotionCode;
    private PromotionType promotionType;
}
