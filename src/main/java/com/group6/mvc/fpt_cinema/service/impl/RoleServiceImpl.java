package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Roles;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl
        extends AbstractCrudService<Roles, Integer>
        implements RoleService {

    public RoleServiceImpl(RoleRepository repository) {
        super(repository);
    }
}
