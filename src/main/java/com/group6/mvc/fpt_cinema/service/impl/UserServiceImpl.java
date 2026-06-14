package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.LoginRequest;
import com.group6.mvc.fpt_cinema.dto.request.RegisterRequest;
import com.group6.mvc.fpt_cinema.dto.request.UpdateUserRequest;
import com.group6.mvc.fpt_cinema.dto.response.LoginResponse;
import com.group6.mvc.fpt_cinema.dto.response.RegisterResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserResponse;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.entity.User_Permission;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.UserMapper;
import com.group6.mvc.fpt_cinema.repository.RolePermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.repository.UserPermissionRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.security.JwtService;
import com.group6.mvc.fpt_cinema.security.JwtService.GeneratedToken;
import com.group6.mvc.fpt_cinema.security.RoleIds;
import com.group6.mvc.fpt_cinema.service.UserService;

@Service
public class UserServiceImpl
        extends AbstractCrudService<User, Integer>
        implements UserService {

    private static final Set<String> VALID_USER_STATUSES =
            Set.of("ACTIVE", "INACTIVE", "LOCKED");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            UserPermissionRepository userPermissionRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserCreateAccountResponse createAccount(CreateAccountRequest request) {
        if (userRepository.getUserByEmail(request.getEmail()) != null) {
            throw new AppException(ErrorCode.EMAIL_EXIST);
        }
        if (userRepository.getUserByPhone(request.getPhone()) != null) {
            throw new AppException(ErrorCode.PHONE_EXIST);
        }

        User user = userMapper.toEntity(request);
        user.setRole(roleRepository.findById(RoleIds.CUSTOMER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));

        User savedUser = userRepository.save(user);
        return userMapper.toCreateAccountResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        User user = userRepository.findOneByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        boolean passwordMatches;
        try {
            passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        } catch (IllegalArgumentException exception) {
            passwordMatches = false;
        }

        if (!passwordMatches) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }

        List<String> permissions = resolveEffectivePermissions(user);
        GeneratedToken generatedToken = jwtService.generateAccessToken(user, permissions);

        return LoginResponse.builder()
                .accessToken(generatedToken.value())
                .expiresIn(generatedToken.expiresIn())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleId(user.getRole().getId())
                .role(user.getRole().getRoleName())
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAllWithRoleOrderById().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer id, UpdateUserRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_USER_DATA);
        }

        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        updateFullName(user, request.getFullName());
        updateEmail(user, request.getEmail());
        updatePhone(user, request.getPhone());
        updatePassword(user, request.getPassword());
        updateStatus(user, request.getStatus());

        if (request.getRoleId() != null) {
            user.setRole(roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse assignRole(Integer userId, Integer roleId) {
        User user = userRepository.findByIdWithRole(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setRole(roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));
        return userMapper.toResponse(userRepository.save(user));
    }

    private List<String> resolveEffectivePermissions(User user) {
        Set<String> effectivePermissions = new TreeSet<>();

        rolePermissionRepository.findAllWithPermissionByRoleId(user.getRole().getId()).stream()
                .map(Role_Permission::getPermission)
                .map(permission -> permission.getPermissionCode())
                .filter(this::hasText)
                .map(String::trim)
                .forEach(effectivePermissions::add);

        for (User_Permission userPermission :
                userPermissionRepository.findAllWithPermissionByUserId(user.getId())) {
            String permissionCode = userPermission.getPermission().getPermissionCode();
            if (!hasText(permissionCode)) {
                continue;
            }

            permissionCode = permissionCode.trim();
            if (Boolean.TRUE.equals(userPermission.getIsGranted())) {
                effectivePermissions.add(permissionCode);
            } else {
                effectivePermissions.remove(permissionCode);
            }
        }

        return List.copyOf(effectivePermissions);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void updateFullName(User user, String fullName) {
        if (hasText(fullName)) {
            user.setFullName(fullName.trim());
        }
    }

    private void updateEmail(User user, String email) {
        if (!hasText(email)) {
            return;
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        userRepository.findOneByEmailIgnoreCase(normalizedEmail)
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new AppException(ErrorCode.EMAIL_EXIST);
                });
        user.setEmail(normalizedEmail);
    }

    private void updatePhone(User user, String phone) {
        if (!hasText(phone)) {
            return;
        }

        String normalizedPhone = phone.trim();
        User existingUser = userRepository.getUserByPhone(normalizedPhone);
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PHONE_EXIST);
        }
        user.setPhone(normalizedPhone);
    }

    private void updatePassword(User user, String password) {
        if (hasText(password)) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }
    }

    private void updateStatus(User user, String status) {
        if (!hasText(status)) {
            return;
        }

        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);
        if (!VALID_USER_STATUSES.contains(normalizedStatus)) {
            throw new AppException(ErrorCode.INVALID_USER_DATA);
        }
        user.setStatus(normalizedStatus);
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.getUserByEmail(request.getEmail()) != null) {
            throw new AppException(ErrorCode.EMAIL_EXIST);
        }
        if (userRepository.getUserByPhone(request.getPhone()) != null) {
            throw new AppException(ErrorCode.PHONE_EXIST);
        }

        User user = userMapper.toEntity(new RegisterRequest(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword()
        ));
        user.setRole(roleRepository.findById(RoleIds.CUSTOMER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));

        User savedUser = userRepository.save(user);
        return userMapper.toRegisterResponse(savedUser);
    }
}
