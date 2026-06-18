package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

import com.group6.mvc.fpt_cinema.enums.MembershipLevel;
import com.group6.mvc.fpt_cinema.enums.UserStatus;

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
    private UserStatus status;
    private Integer rewardPoints;
    private MembershipLevel membershipLevel;
    private Integer roleId;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
