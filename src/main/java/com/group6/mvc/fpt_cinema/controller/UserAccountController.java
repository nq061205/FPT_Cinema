package com.group6.mvc.fpt_cinema.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserAccountController {

    private UserService userService;

    public UserAccountController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ApiResponse<UserCreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        UserCreateAccountResponse response = userService.createAccount(request);
        ApiResponse<UserCreateAccountResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Account created successfully!");
        apiResponse.setResult(response);
        return apiResponse;
    }

}
