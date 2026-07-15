package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.CreateProductRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateProductRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductDetailRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductListRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateProductResponse;
import com.group6.mvc.fpt_cinema.dto.response.UpdateProductResponse;
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

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('PRODUCT_CREATE')")
    public ApiResponse<CreateProductResponse> createProduct(
            @RequestBody CreateProductRequest request) {

        ApiResponse<CreateProductResponse> response = new ApiResponse<>();
        response.setMessage("Product created successfully!");
        response.setResult(productService.createProduct(request));
        return response;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('PRODUCT_UPDATE')")
    public ApiResponse<UpdateProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestBody UpdateProductRequest request) {

        ApiResponse<UpdateProductResponse> response = new ApiResponse<>();
        response.setMessage("Product updated successfully!");
        response.setResult(productService.updateProduct(id, request));
        return response;
    }
}
