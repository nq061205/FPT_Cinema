package com.group6.mvc.fpt_cinema.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group6.mvc.fpt_cinema.entity.User_Promotion;

public interface UserPromotionRepository extends JpaRepository<User_Promotion, Integer> {
    Page<User_Promotion> findByUserId(Integer userId, Pageable pageable);
}
