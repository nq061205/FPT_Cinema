package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ReplaceRolePermissionsRequest;
import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.service.RolePermissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_PERMISSION_ASSIGN')")
    public ApiResponse<List<PermissionResponse>> getRolePermissions(@PathVariable Integer roleId) {
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Role permissions retrieved successfully")
                .result(rolePermissionService.getRolePermissions(roleId))
                .build();
    }

    @PutMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_PERMISSION_ASSIGN')")
    public ApiResponse<RolePermissionResponse> assignPermissionToRole(
            @PathVariable Integer roleId,
            @PathVariable Integer permissionId) {
        return ApiResponse.<RolePermissionResponse>builder()
                .message("Permission assigned to role successfully")
                .result(rolePermissionService.assignPermission(roleId, permissionId))
                .build();
    }

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_PERMISSION_ASSIGN')")
    public ApiResponse<List<PermissionResponse>> replaceRolePermissions(
            @PathVariable Integer roleId,
            @Valid @RequestBody ReplaceRolePermissionsRequest request) {
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Role permissions updated successfully")
                .result(rolePermissionService.replaceRolePermissions(roleId, request.getPermissionIds()))
                .build();
    }
}
