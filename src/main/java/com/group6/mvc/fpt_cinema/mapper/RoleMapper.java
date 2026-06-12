package com.group6.mvc.fpt_cinema.mapper;

import com.group6.mvc.fpt_cinema.dto.response.RoleResponse;
import com.group6.mvc.fpt_cinema.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    RoleResponse toRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleName(role.getRoleName());
        return roleResponse;
    }

}
