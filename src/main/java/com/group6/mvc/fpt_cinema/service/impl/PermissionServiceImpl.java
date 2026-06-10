package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.repository.PermissionRepository;
import com.group6.mvc.fpt_cinema.service.PermissionService;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl
        extends AbstractCrudService<Permission, Integer>
        implements PermissionService {

    public PermissionServiceImpl(PermissionRepository repository) {
        super(repository);
    }
}
