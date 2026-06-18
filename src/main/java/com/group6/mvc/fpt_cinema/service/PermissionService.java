package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.CreatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Permission;

public interface PermissionService extends CrudService<Permission, Integer> {
    PermissionResponse createPermission(CreatePermissionRequest request);

    PermissionResponse updatePermission(Integer id, UpdatePermissionRequest request);
}
