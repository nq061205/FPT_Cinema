package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.response.RolePermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.PermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RolePermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionServiceImpl
        extends AbstractCrudService<Role_Permission, Integer>
        implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionServiceImpl(
            RolePermissionRepository repository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        super(repository);
        this.rolePermissionRepository = repository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public RolePermissionResponse assignPermission(
            Integer roleId,
            Integer permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        Role_Permission rolePermission = rolePermissionRepository
                .findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseGet(() -> {
                    Role_Permission assignment = new Role_Permission();
                    assignment.setRole(role);
                    assignment.setPermission(permission);
                    return rolePermissionRepository.save(assignment);
                });

        return RolePermissionResponse.builder()
                .id(rolePermission.getId())
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .permissionId(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .build();
    }
}
