package com.group6.mvc.fpt_cinema.dto.response;

import com.group6.mvc.fpt_cinema.enums.MembershipLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String fullName;
    private Integer roleId;
    private String email;
    private String phone;
    private Integer rewardPoints;
    private MembershipLevel membershipLevel;
}
