package com.group6.mvc.fpt_cinema.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private int roleId;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String status;
    private Integer rewardPoints;
    private String membershipLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
