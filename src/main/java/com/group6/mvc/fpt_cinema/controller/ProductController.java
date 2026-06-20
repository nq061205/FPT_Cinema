package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductDetailRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductDetailResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductListResponse;
import com.group6.mvc.fpt_cinema.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/list")
    public ApiResponse<List<ViewProductListResponse>> viewProductList(
            @RequestBody(required = false) ViewProductListRequest request) {

        ApiResponse<List<ViewProductListResponse>> response = new ApiResponse<>();
        response.setMessage("Product list retrieved successfully!");
        response.setResult(productService.viewProductList(request));
        return response;
    }

    @PostMapping("/detail")
    public ApiResponse<ViewProductDetailResponse> viewProductDetail(
            @RequestBody ViewProductDetailRequest request) {

        ApiResponse<ViewProductDetailResponse> response = new ApiResponse<>();
        response.setMessage("Product details retrieved successfully!");
        response.setResult(productService.viewProductDetail(request));
        return response;
    }

}
