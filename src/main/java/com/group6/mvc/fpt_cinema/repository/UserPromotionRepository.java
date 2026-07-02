package com.group6.mvc.fpt_cinema.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.User_Promotion;

@Repository
public interface UserPromotionRepository extends JpaRepository<User_Promotion, Integer> {

    Page<User_Promotion> findByUserId(Integer userId, Pageable pageable);

    Optional<User_Promotion> findByUserIdAndPromotionId(Integer userId, Integer promotionId);
}
