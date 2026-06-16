package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.response.UserPermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.entity.User_Permission;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.PermissionRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.repository.UserPermissionRepository;
import com.group6.mvc.fpt_cinema.service.UserPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPermissionServiceImpl
        extends AbstractCrudService<User_Permission, Integer>
        implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public UserPermissionServiceImpl(
            UserPermissionRepository repository,
            UserRepository userRepository,
            PermissionRepository permissionRepository) {
        super(repository);
        this.userPermissionRepository = repository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public UserPermissionResponse assignPermission(
            Integer userId,
            Integer permissionId,
            Boolean isGranted) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        boolean granted = isGranted == null || isGranted;
        User_Permission userPermission = userPermissionRepository
                .findByUserIdAndPermissionId(userId, permissionId)
                .orElseGet(() -> {
                    User_Permission assignment = new User_Permission();
                    assignment.setUser(user);
                    assignment.setPermission(permission);
                    return assignment;
                });
        userPermission.setIsGranted(granted);
        User_Permission savedAssignment = userPermissionRepository.save(userPermission);

        return UserPermissionResponse.builder()
                .id(savedAssignment.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .permissionId(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .isGranted(savedAssignment.getIsGranted())
                .build();
    }
}
