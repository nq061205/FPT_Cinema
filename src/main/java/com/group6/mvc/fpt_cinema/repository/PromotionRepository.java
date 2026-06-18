package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    Promotion findByPromotionCode(Integer id);
}
