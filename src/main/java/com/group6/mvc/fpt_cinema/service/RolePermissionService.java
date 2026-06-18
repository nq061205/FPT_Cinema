package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;

public interface RolePermissionService extends CrudService<Role_Permission, Integer> {
    RolePermissionResponse assignPermission(Integer roleId, Integer permissionId);
}
