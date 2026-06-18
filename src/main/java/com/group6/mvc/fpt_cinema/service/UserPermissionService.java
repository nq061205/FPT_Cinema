package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.response.UserPermissionResponse;
import com.group6.mvc.fpt_cinema.entity.User_Permission;

public interface UserPermissionService extends CrudService<User_Permission, Integer> {
    UserPermissionResponse assignPermission(
            Integer userId,
            Integer permissionId,
            Boolean isGranted);
}
