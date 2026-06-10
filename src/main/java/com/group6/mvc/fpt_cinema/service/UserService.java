package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.entity.User;

public interface UserService extends CrudService<User, Integer> {
    UserCreateAccountResponse createAccount(CreateAccountRequest request);
}
