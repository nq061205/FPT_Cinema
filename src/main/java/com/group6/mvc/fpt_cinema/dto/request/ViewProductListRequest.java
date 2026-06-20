package com.group6.mvc.fpt_cinema.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewProductListRequest {
    private Integer page = 0;
    private Integer size = 10;
    private String productType;
}
