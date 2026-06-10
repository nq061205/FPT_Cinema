package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.User_Permission;
import com.group6.mvc.fpt_cinema.repository.UserPermissionRepository;
import com.group6.mvc.fpt_cinema.service.UserPermissionService;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionServiceImpl
        extends AbstractCrudService<User_Permission, Integer>
        implements UserPermissionService {

    public UserPermissionServiceImpl(UserPermissionRepository repository) {
        super(repository);
    }
}
