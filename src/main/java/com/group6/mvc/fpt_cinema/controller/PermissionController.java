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
import com.group6.mvc.fpt_cinema.dto.request.CreatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.service.PermissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Permissions retrieved successfully")
                .result(permissionService.getPermissions())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_CREATE')")
    public ApiResponse<PermissionResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission created successfully")
                .result(permissionService.createPermission(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PERMISSION_UPDATE')")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission updated successfully")
                .result(permissionService.updatePermission(id, request))
                .build();
    }
}
