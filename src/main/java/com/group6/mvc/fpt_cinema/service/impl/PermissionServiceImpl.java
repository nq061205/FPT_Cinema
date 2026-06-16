package com.group6.mvc.fpt_cinema.service.impl;

import java.util.Locale;

import com.group6.mvc.fpt_cinema.dto.request.CreatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdatePermissionRequest;
import com.group6.mvc.fpt_cinema.dto.response.PermissionResponse;
import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.repository.PermissionRepository;
import com.group6.mvc.fpt_cinema.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl
        extends AbstractCrudService<Permission, Integer>
        implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository repository) {
        super(repository);
        this.permissionRepository = repository;
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (request == null
                || !hasText(request.getPermissionCode())
                || !hasText(request.getPermissionName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION_DATA);
        }

        String permissionCode = normalizePermissionCode(request.getPermissionCode());
        ensureValidPermissionCode(permissionCode);
        ensurePermissionCodeAvailable(permissionCode, null);

        Permission permission = new Permission();
        permission.setPermissionCode(permissionCode);
        permission.setPermissionName(request.getPermissionName().trim());
        permission.setDescription(normalizeOptionalText(request.getDescription()));
        return toResponse(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(
            Integer id,
            UpdatePermissionRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_PERMISSION_DATA);
        }

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        if (hasText(request.getPermissionCode())) {
            String permissionCode = normalizePermissionCode(request.getPermissionCode());
            ensureValidPermissionCode(permissionCode);
            ensurePermissionCodeAvailable(permissionCode, id);
            permission.setPermissionCode(permissionCode);
        }
        if (hasText(request.getPermissionName())) {
            permission.setPermissionName(request.getPermissionName().trim());
        }
        if (request.getDescription() != null) {
            permission.setDescription(normalizeOptionalText(request.getDescription()));
        }

        return toResponse(permissionRepository.save(permission));
    }

    private void ensurePermissionCodeAvailable(String permissionCode, Integer currentId) {
        permissionRepository.findByPermissionCodeIgnoreCase(permissionCode)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.PERMISSION_CODE_EXIST);
                });
    }

    private String normalizePermissionCode(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private void ensureValidPermissionCode(String permissionCode) {
        if (!permissionCode.matches("[A-Z0-9_]+")) {
            throw new AppException(ErrorCode.INVALID_PERMISSION_DATA);
        }
    }

    private String normalizeOptionalText(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .build();
    }
}
