package com.group6.mvc.fpt_cinema.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.service.RoleService;

@Service
public class RoleServiceImpl
        extends AbstractCrudService<Role, Integer>
        implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repository) {
        super(repository);
    }

    @Override
    public Role getRoleById(int id) {
        return roleRepository.findById(id).get();
    }
}
