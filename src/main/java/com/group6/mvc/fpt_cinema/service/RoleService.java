package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.entity.Role;

public interface RoleService extends CrudService<Role, Integer> {
    Role getRoleById(int id);
}
