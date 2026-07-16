package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.response.RoleResponse;
import com.group6.mvc.fpt_cinema.entity.Role;

public interface RoleService extends CrudService<Role, Integer> {
    Role getRoleById(int id);

    List<RoleResponse> getRoles();

    RoleResponse getRoleResponseById(Integer id);
}
