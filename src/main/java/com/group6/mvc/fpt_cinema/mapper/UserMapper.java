package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserChangePasswordResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserProfileResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserResponse;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.security.EncryptPassword;

@Component
public class UserMapper {

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

    public UserProfileResponse toUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRewardPoints(user.getRewardPoints());
        response.setMembershipLevel(user.getMembershipLevel());
        return response;
    }

    public UserChangePasswordResponse toChangePasswordResponse(User user) {
        UserChangePasswordResponse response = new UserChangePasswordResponse();
        response.setEmail(user.getEmail());
        return response;
    }
}
