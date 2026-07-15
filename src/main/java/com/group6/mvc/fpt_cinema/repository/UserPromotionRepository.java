package com.group6.mvc.fpt_cinema.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.User_Promotion;

@Repository
public interface UserPromotionRepository extends JpaRepository<User_Promotion, Integer> {

    Page<User_Promotion> findByUserId(Integer userId, Pageable pageable);

    Optional<User_Promotion> findByUserIdAndPromotionId(Integer userId, Integer promotionId);

    @Query("SELECT up FROM User_Promotion up WHERE up.user.id = :userId " +
           "AND up.status = 'AVAILABLE' " +
           "AND up.promotion.isActive = true " +
           "AND up.promotion.startDate <= :now " +
           "AND up.promotion.endDate >= :now")
    Page<User_Promotion> findUsablePromotions(
            @Param("userId") Integer userId,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}
