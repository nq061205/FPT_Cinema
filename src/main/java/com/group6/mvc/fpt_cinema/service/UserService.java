package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.LoginRequest;
import com.group6.mvc.fpt_cinema.dto.request.RegisterRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateUserRequest;
import com.group6.mvc.fpt_cinema.dto.request.UserChangePasswordRequest;
import com.group6.mvc.fpt_cinema.dto.request.UserProfileEditRequest;
import com.group6.mvc.fpt_cinema.dto.response.LoginResponse;
import com.group6.mvc.fpt_cinema.dto.response.RegisterResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserChangePasswordResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserProfileResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserResponse;
import com.group6.mvc.fpt_cinema.entity.User;

public interface UserService extends CrudService<User, Integer> {
    UserCreateAccountResponse createAccount(CreateAccountRequest request);

    LoginResponse login(LoginRequest request);

    List<UserResponse> getUsers();

    RegisterResponse register(RegisterRequest request);

    UserResponse getUserById(Integer id);

    UserResponse updateUser(Integer id, UpdateUserRequest request);

    UserResponse assignRole(Integer userId, Integer roleId);

    UserProfileResponse getProfile(String email);

    UserProfileResponse editProfile(String email, UserProfileEditRequest request);

    UserChangePasswordResponse changePassword(String email, UserChangePasswordRequest request);
}
