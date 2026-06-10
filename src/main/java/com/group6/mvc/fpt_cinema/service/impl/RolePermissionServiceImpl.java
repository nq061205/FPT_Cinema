package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Role_Permission;
import com.group6.mvc.fpt_cinema.repository.RolePermissionRepository;
import com.group6.mvc.fpt_cinema.service.RolePermissionService;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl
        extends AbstractCrudService<Role_Permission, Integer>
        implements RolePermissionService {

    public RolePermissionServiceImpl(RolePermissionRepository repository) {
        super(repository);
    }
}
