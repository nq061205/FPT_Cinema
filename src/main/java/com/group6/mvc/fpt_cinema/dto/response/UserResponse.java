package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private Integer rewardPoints;
    private String membershipLevel;
    private Integer roleId;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
