package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.LoginRequest;
import com.group6.mvc.fpt_cinema.dto.request.UserChangePasswordRequest;
import com.group6.mvc.fpt_cinema.dto.request.UserProfileEditRequest;
import com.group6.mvc.fpt_cinema.dto.response.LoginResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserChangePasswordResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserCreateAccountResponse;
import com.group6.mvc.fpt_cinema.dto.response.UserProfileResponse;
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

    private List<String> resolveEffectivePermissions(User user) {
        Set<String> effectivePermissions = new TreeSet<>();

        rolePermissionRepository.findAllWithPermissionByRoleId(user.getRole().getId()).stream()
                .map(Role_Permission::getPermission)
                .map(permission -> permission.getPermissionCode())
                .filter(this::hasText)
                .map(String::trim)
                .forEach(effectivePermissions::add);

        for (User_Permission userPermission : userPermissionRepository.findAllWithPermissionByUserId(user.getId())) {
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

    @Override
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse editProfile(String email, UserProfileEditRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        return userMapper.toUserProfileResponse(savedUser);
    }

    @Override
    public UserChangePasswordResponse changePassword(String email, UserChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        User savedUser = userRepository.save(user);

        return userMapper.toChangePasswordResponse(savedUser);
    }
}
