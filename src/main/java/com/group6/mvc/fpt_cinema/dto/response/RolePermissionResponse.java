package com.group6.mvc.fpt_cinema.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RolePermissionResponse {
    private Integer id;
    private Integer roleId;
    private String roleName;
    private Integer permissionId;
    private String permissionCode;
}
