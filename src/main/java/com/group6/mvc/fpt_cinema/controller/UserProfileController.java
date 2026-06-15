package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.UserChangePasswordRequest;
import com.group6.mvc.fpt_cinema.dto.request.UserProfileEditRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserChangePasswordResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserProfileResponse;
import com.group6.mvc.fpt_cinema.service.UserService;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<UserProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        UserProfileResponse profile = userService.getProfile(email);

        ApiResponse<UserProfileResponse> apiResponse = new ApiResponse<>();
        apiResponse.setCode(201);
        apiResponse.setMessage("User profile retrieved successfully!");
        apiResponse.setResult(profile);
        return apiResponse;
    }

    @PatchMapping("/edit")
    public ApiResponse<UserProfileResponse> editProfile(Authentication authentication,
            @RequestBody UserProfileEditRequest request) {
        String email = authentication.getName();
        UserProfileResponse updatedProfile = userService.editProfile(email, request);

        ApiResponse<UserProfileResponse> apiResponse = new ApiResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("User profile updated successfully!");
        apiResponse.setResult(updatedProfile);
        return apiResponse;
    }

    @PatchMapping("/change-password")
    public ApiResponse<UserChangePasswordResponse> changePassword(Authentication authentication,
            @RequestBody UserChangePasswordRequest request) {

        String email = authentication.getName();
        UserChangePasswordResponse response = userService.changePassword(email, request);

        ApiResponse<UserChangePasswordResponse> apiResponse = new ApiResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("Password changed successfully!");
        apiResponse.setResult(response);
        return apiResponse;
    }
}