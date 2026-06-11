package com.group6.mvc.fpt_cinema.mapper;

import com.group6.mvc.fpt_cinema.security.EncryptPassword;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RoleRepository roleRepository;

    public UserMapper() {
    }

    public UserCreateAccountResponse toCreateAccountResponse(User user) {
        UserCreateAccountResponse response = new UserCreateAccountResponse();
        response.setEmail(user.getEmail());
        return response;
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
