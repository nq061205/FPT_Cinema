package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.response.RoleResponse;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
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

    @Override
    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleResponseById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return toResponse(role);
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .build();
    }
}
