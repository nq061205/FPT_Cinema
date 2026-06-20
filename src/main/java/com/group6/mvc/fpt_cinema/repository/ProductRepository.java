package com.group6.mvc.fpt_cinema.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByProductTypeAndIsActiveTrue(String productType, Pageable pageable);

    Optional<Product> findByIdAndIsActiveTrue(Integer id);
}
