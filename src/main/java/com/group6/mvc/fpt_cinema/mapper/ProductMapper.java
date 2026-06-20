package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.response.ViewProductDetailResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductListResponse;
import com.group6.mvc.fpt_cinema.entity.Product;

@Component
public class ProductMapper {

    public ViewProductListResponse toViewProductListResponse(Product product) {
        ViewProductListResponse response = new ViewProductListResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setProductType(product.getProductType());
        response.setPrice(product.getPrice());
        response.setIsActive(product.getIsActive());
        return response;
    }

    public ViewProductDetailResponse toViewProductDetailResponse(Product product) {
        ViewProductDetailResponse response = new ViewProductDetailResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setProductType(product.getProductType());
        response.setPrice(product.getPrice());
        response.setIsActive(product.getIsActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
