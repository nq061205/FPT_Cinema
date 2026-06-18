package com.group6.mvc.fpt_cinema.dto.response;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewUserPromotionListResponse {
    private List<ViewUserPromotionResponse> promotions;
}
