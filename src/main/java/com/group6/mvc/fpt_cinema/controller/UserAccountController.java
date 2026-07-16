package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.AssignUserPermissionRequest;
import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateUserRequest;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserPermissionResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserResponse;
import com.group6.mvc.fpt_cinema.service.UserPermissionService;
import com.group6.mvc.fpt_cinema.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserAccountController {

    private final UserService userService;
    private final UserPermissionService userPermissionService;

    public UserAccountController(
            UserService userService,
            UserPermissionService userPermissionService) {
        this.userService = userService;
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ApiResponse<UserCreateAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        UserCreateAccountResponse response = userService.createAccount(request);
        ApiResponse<UserCreateAccountResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Account created successfully!");
        apiResponse.setResult(response);
        return apiResponse;
    }

    @GetMapping("/user-list")
    @PreAuthorize("hasAuthority('USER_VIEW_LIST')")
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Users retrieved successfully")
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW_DETAIL')")
    public ApiResponse<UserResponse> getUserById(@PathVariable Integer id) {
        return ApiResponse.<UserResponse>builder()
                .message("User retrieved successfully")
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('USER_VIEW_DETAIL')")
    public ApiResponse<List<UserPermissionResponse>> getUserPermissions(@PathVariable Integer id) {
        return ApiResponse.<List<UserPermissionResponse>>builder()
                .message("User permissions retrieved successfully")
                .result(userPermissionService.getUserPermissions(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("User updated successfully")
                .result(userService.updateUser(id, request))
                .build();
    }

    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_PERMISSION_ASSIGN')")
    public ApiResponse<List<UserPermissionResponse>> getPermissionsByUser(@PathVariable Integer userId) {
        return ApiResponse.<List<UserPermissionResponse>>builder()
                .message("User permissions retrieved successfully")
                .result(userPermissionService.getPermissionsByUser(userId))
                .build();
    }

    @PutMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_PERMISSION_ASSIGN')")
    public ApiResponse<UserPermissionResponse> assignPermissionToUser(
            @PathVariable Integer userId,
            @PathVariable Integer permissionId,
            @RequestBody(required = false) AssignUserPermissionRequest request) {
        Boolean isGranted = request == null ? null : request.getIsGranted();
        return ApiResponse.<UserPermissionResponse>builder()
                .message("Permission assigned to user successfully")
                .result(userPermissionService.assignPermission(
                        userId,
                        permissionId,
                        isGranted))
                .build();
    }

    @PutMapping("/{userId}/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('USER_ROLE_ASSIGN')")
    public ApiResponse<UserResponse> assignRoleToUser(
            @PathVariable Integer userId,
            @PathVariable Integer roleId) {
        return ApiResponse.<UserResponse>builder()
                .message("Role assigned to user successfully")
                .result(userService.assignRole(userId, roleId))
                .build();
    }
}
