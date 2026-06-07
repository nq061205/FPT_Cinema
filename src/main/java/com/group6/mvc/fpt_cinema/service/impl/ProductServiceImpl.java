package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Products;
import com.group6.mvc.fpt_cinema.repository.ProductRepository;
import com.group6.mvc.fpt_cinema.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl
        extends AbstractCrudService<Products, Integer>
        implements ProductService {

    public ProductServiceImpl(ProductRepository repository) {
        super(repository);
    }
}
