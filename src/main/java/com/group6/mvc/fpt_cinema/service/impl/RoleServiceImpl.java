package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.response.RoleResponse;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.mapper.RoleMapper;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
