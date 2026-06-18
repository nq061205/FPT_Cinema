package com.group6.mvc.fpt_cinema.service;

import org.springframework.data.domain.Pageable;

import com.group6.mvc.fpt_cinema.dto.response.ViewUserPromotionListResponse;
import com.group6.mvc.fpt_cinema.entity.User_Promotion;

public interface UserPromotionService extends CrudService<User_Promotion, Integer> {
     ViewUserPromotionListResponse viewUserPromotions(
            Integer userId,
            Pageable pageable);

}
