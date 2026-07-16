package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;

public interface RolePermissionService extends CrudService<Role_Permission, Integer> {
    RolePermissionResponse assignPermission(Integer roleId, Integer permissionId);

    List<PermissionResponse> getRolePermissions(Integer roleId);

    List<PermissionResponse> replaceRolePermissions(Integer roleId, List<Integer> permissionIds);
}
