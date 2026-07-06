package com.group6.mvc.fpt_cinema.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.CreateProductRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateProductRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductDetailRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewProductListRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateProductResponse;
import com.group6.mvc.fpt_cinema.dto.response.UpdateProductResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductDetailResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewProductListResponse;
import com.group6.mvc.fpt_cinema.entity.Product;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.ProductMapper;
import com.group6.mvc.fpt_cinema.repository.ProductRepository;
import com.group6.mvc.fpt_cinema.service.ProductService;

@Service
public class ProductServiceImpl
        extends AbstractCrudService<Product, Integer>
        implements ProductService {

    private static final Set<String> VALID_TYPES = Set.of("FOOD", "BEVERAGE", "COMBO");

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        super(productRepository);
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ViewProductListResponse> viewProductList(ViewProductListRequest request) {
        int page = request == null || request.getPage() == null ? 0 : Math.max(request.getPage(), 0);
        int size = request == null || request.getSize() == null ? 10 : Math.max(request.getSize(), 1);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));

        String type = request != null && request.getProductType() != null
                ? request.getProductType().toUpperCase().trim()
                : null;

        if (type != null && !VALID_TYPES.contains(type)) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_TYPE);
        }

        Page<Product> productPage = type != null
                ? productRepository.findByProductTypeAndIsActiveTrue(type, pageable)
                : productRepository.findByIsActiveTrue(pageable);

        return productPage.getContent()
                .stream()
                .map(productMapper::toViewProductListResponse)
                .toList();
    }

    @Override
    public ViewProductDetailResponse viewProductDetail(ViewProductDetailRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toViewProductDetailResponse(product);
    }

    @Override
    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        String type = request.getProductType() != null
                ? request.getProductType().toUpperCase().trim()
                : null;
        if (type == null || !VALID_TYPES.contains(type)) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_TYPE);
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_PRICE);
        }

        Product product = productMapper.toProduct(request);
        return productMapper.toCreateProductResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public UpdateProductResponse updateProduct(Integer productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            product.setName(request.getName());
        }

        if (request.getProductType() != null) {
            String type = request.getProductType().toUpperCase().trim();
            if (!VALID_TYPES.contains(type)) {
                throw new AppException(ErrorCode.INVALID_PRODUCT_TYPE);
            }
            product.setProductType(type);
        }

        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AppException(ErrorCode.INVALID_PRICE);
            }
            product.setPrice(request.getPrice());
        }

        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return productMapper.toUpdateProductResponse(productRepository.save(product));
    }
}
