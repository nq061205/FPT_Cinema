package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
