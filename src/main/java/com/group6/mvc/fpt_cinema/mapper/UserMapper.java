package com.group6.mvc.fpt_cinema.mapper;

import org.springframework.stereotype.Component;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;

@Component
public class UserMapper {

    private RoleRepository roleRepository;

    private UserMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public UserCreateAccountResponse toCreateAccountResponse(User user) {
        UserCreateAccountResponse response = new UserCreateAccountResponse();
        response.setUsername(user.getFullName());
        return response;
    }

    public User toEntity(CreateAccountRequest request) {
        User user = new User();
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(request.getPasswordHash());
        user.setStatus(request.getStatus());
        user.setRewardPoints(request.getRewardPoints());
        user.setMembershipLevel(request.getMembershipLevel());
        return user;
    }
}
