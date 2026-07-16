package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;

public interface RolePermissionService extends CrudService<Role_Permission, Integer> {
    // Phương thức chính để lấy danh sách quyền theo vai trò
    List<RolePermissionResponse> getPermissionsByRole(Integer roleId);

    // Gán một quyền cụ thể vào vai trò
    RolePermissionResponse assignPermission(Integer roleId, Integer permissionId);

    // Thay thế toàn bộ danh sách quyền của vai trò
    List<PermissionResponse> replaceRolePermissions(Integer roleId, List<Integer> permissionIds);
}