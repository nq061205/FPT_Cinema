package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.ViewProductDetailRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductListRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductDetailResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductListResponse;
import com.group6.mvc.fpt_cinema.entity.Product;

public interface ProductService extends CrudService<Product, Integer> {

    List<ViewProductListResponse> viewProductList(ViewProductListRequest request);

    ViewProductDetailResponse viewProductDetail(ViewProductDetailRequest request);


}
