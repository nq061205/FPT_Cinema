package com.group6.mvc.fpt_cinema.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.service.RolePermissionService;

@RestController
@RequestMapping("/api/roles")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
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
}
