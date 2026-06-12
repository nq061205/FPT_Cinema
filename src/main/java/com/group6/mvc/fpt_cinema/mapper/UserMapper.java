package com.group6.mvc.fpt_cinema.mapper;

import com.group6.mvc.fpt_cinema.security.EncryptPassword;
import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserResponse;
import com.group6.mvc.fpt_cinema.entity.User;

@Component
public class UserMapper {

    public UserMapper() {
    }

    public UserCreateAccountResponse toCreateAccountResponse(User user) {
        UserCreateAccountResponse response = new UserCreateAccountResponse();
        response.setEmail(user.getEmail());
        return response;
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .rewardPoints(user.getRewardPoints())
                .membershipLevel(user.getMembershipLevel())
                .roleId(user.getRole().getId())
                .role(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(CreateAccountRequest request) {
        User user = new User();
        user.setPasswordHash(EncryptPassword.encryptPassword(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return user;
    }
}
